package com.coco802.client.gui;

import com.coco802.client.Client802;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Installe des mods depuis Modrinth (officiel) quand TU cliques "Installer" :
 * HTTPS, vérification du hash SHA-512 donné par l'API, fichier écrit dans mods/, rien de caché.
 */
public final class ModInstaller {
    public record Entry(String slug, String modId, String name, String description) {}

    private static final Map<String, String> STATUS = new ConcurrentHashMap<>();
    private static final String UA = "802-client/1.0 (mod manager)";

    private static final List<Entry> CATALOG = List.of(
        new Entry("sodium", "sodium", "Sodium", "Moteur de rendu très rapide"),
        new Entry("lithium", "lithium", "Lithium", "Optimise la logique du jeu"),
        new Entry("ferrite-core", "ferritecore", "FerriteCore", "Réduit l'utilisation de la mémoire"),
        new Entry("immediatelyfast", "immediatelyfast", "ImmediatelyFast", "Accélère le rendu de l'interface"),
        new Entry("entityculling", "entityculling", "Entity Culling", "Ne dessine pas ce qui est caché"),
        new Entry("moreculling", "moreculling", "More Culling", "Cache plus de faces invisibles"),
        new Entry("modmenu", "modmenu", "Mod Menu", "Liste des mods installés"),
        new Entry("iris", "iris", "Iris Shaders", "Support des shaders"),
        new Entry("sodium-extra", "", "Sodium Extra", "Options de rendu en plus pour Sodium"),
        new Entry("reeses-sodium-options", "", "Reese's Sodium Options", "Menu d'options de Sodium"),
        new Entry("dynamic-fps", "", "Dynamic FPS", "Réduit les FPS en arrière-plan"),
        new Entry("modernfix", "modernfix", "ModernFix", "Démarrage et mémoire optimisés"),
        new Entry("memoryleakfix", "", "Memory Leak Fix", "Corrige des fuites de mémoire"),
        new Entry("c2me-fabric", "", "C2ME", "Chargement de chunks multi-thread"),
        new Entry("krypton", "krypton", "Krypton", "Optimise la pile réseau"),
        new Entry("enhanced-block-entities", "", "Enhanced Block Entities", "Coffres et blocs plus rapides"),
        new Entry("no-chat-reports", "", "No Chat Reports", "Désactive les signatures de chat"),
        new Entry("appleskin", "appleskin", "AppleSkin", "Infos de faim et saturation"),
        new Entry("xaeros-minimap", "", "Xaero's Minimap", "Minimap"),
        new Entry("jade", "", "Jade", "Infos sur le bloc visé"),
        new Entry("mouse-tweaks", "mousetweaks", "Mouse Tweaks", "Inventaire plus rapide à la souris"),
        new Entry("zoomify", "", "Zoomify", "Zoom configurable"),
        new Entry("lambdynamiclights", "", "LambDynamicLights", "Lumière dynamique"),
        new Entry("continuity", "continuity", "Continuity", "Textures connectées"),
        new Entry("cloth-config", "", "Cloth Config", "Bibliothèque de configuration"),
        new Entry("yacl", "", "YetAnotherConfigLib", "Bibliothèque de configuration")
    );

    private ModInstaller() {}

    public static List<Entry> entries() {
        List<Entry> all = new ArrayList<>(CATALOG);
        for (String slug : Client802.CONFIG.extraMods) {
            if (slug == null || slug.isBlank()) continue;
            all.add(new Entry(slug.trim(), "", slug.trim(), "Mod Modrinth ajouté dans 802client.json"));
        }
        return all;
    }

    public static String status(Entry e) {
        if (!e.modId().isEmpty() && FabricLoader.getInstance().isModLoaded(e.modId())) return "Installé";
        String s = STATUS.get(e.slug());
        return s == null ? "Installer" : s;
    }

    public static boolean canInstall(Entry e) {
        return status(e).equals("Installer") || status(e).startsWith("Échec") || status(e).startsWith("Indisponible");
    }

    public static void install(Entry e) {
        STATUS.put(e.slug(), "Téléchargement...");
        Thread t = new Thread(() -> {
            try {
                run(e);
            } catch (Exception ex) {
                STATUS.put(e.slug(), "Échec : " + ex.getClass().getSimpleName());
            }
        }, "802-mod-installer");
        t.setDaemon(true);
        t.start();
    }

    // ---------- recherche Modrinth (tout le catalogue, par pages de 40) ----------
    public static final String[] CATEGORY_IDS = {"", "optimization", "utility", "decoration", "technology", "adventure",
        "equipment", "library", "magic", "mobs", "storage", "game-mechanics", "worldgen", "social", "economy",
        "food", "transportation", "management", "cursed"};
    public static final String[] CATEGORY_LABELS = {"Toutes", "Optimisation", "Utilitaire", "Décoration", "Technologie",
        "Aventure", "Équipement", "Bibliothèque", "Magie", "Mobs", "Stockage", "Mécaniques", "Génération", "Social",
        "Économie", "Nourriture", "Transport", "Gestion", "Bizarre"};

    private static volatile List<Entry> searchResults = List.of();
    private static volatile String searchState = "";
    private static volatile boolean searching;
    private static volatile int total;
    private static volatile int generation;
    private static volatile String curQuery = "";
    private static volatile String curCat = "";

    public static List<Entry> searchResults() { return searchResults; }
    public static String searchState() { return searchState; }
    public static boolean isSearching() { return searching; }
    public static boolean hasMore() { return !searching && !searchResults.isEmpty() && searchResults.size() < total; }

    public static void startSearch(String query, String category) {
        generation++;
        curQuery = query;
        curCat = category;
        searchResults = List.of();
        total = 0;
        load(generation, 0);
    }

    public static void loadMore() {
        if (searching) return;
        load(generation, searchResults.size());
    }

    private static void load(final int gen, final int offset) {
        searching = true;
        searchState = "Recherche...";
        final String query = curQuery;
        final String cat = curCat;
        Thread t = new Thread(() -> {
            try {
                HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
                StringBuilder f = new StringBuilder("[[\"project_type:mod\"],[\"versions:1.21.11\"],[\"categories:fabric\"]");
                if (!cat.isEmpty()) f.append(",[\"categories:").append(cat).append("\"]");
                f.append("]");
                String index = query.isBlank() ? "downloads" : "relevance";
                String url = "https://api.modrinth.com/v2/search?limit=40&offset=" + offset + "&index=" + index
                    + "&facets=" + URLEncoder.encode(f.toString(), StandardCharsets.UTF_8)
                    + "&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
                HttpResponse<String> res = http.send(HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
                if (gen != generation) return;
                if (res.statusCode() != 200) {
                    searchState = "Échec : HTTP " + res.statusCode();
                    return;
                }
                JsonObject body = JsonParser.parseString(res.body()).getAsJsonObject();
                JsonArray hits = body.getAsJsonArray("hits");
                List<Entry> merged = new ArrayList<>(searchResults);
                for (JsonElement el : hits) {
                    JsonObject h = el.getAsJsonObject();
                    merged.add(new Entry(h.get("slug").getAsString(), "", h.get("title").getAsString(),
                        h.get("description").getAsString()));
                }
                if (gen != generation) return;
                total = body.has("total_hits") ? body.get("total_hits").getAsInt() : merged.size();
                searchResults = merged;
                searchState = merged.isEmpty() ? "Aucun résultat pour 1.21.11 / Fabric." : merged.size() + " / " + total + " mods";
            } catch (Exception ex) {
                if (gen == generation) searchState = "Échec : " + ex.getClass().getSimpleName();
            } finally {
                if (gen == generation) searching = false;
            }
        }, "802-mod-search");
        t.setDaemon(true);
        t.start();
    }

    // ---------- installation (avec dépendances obligatoires) ----------
    private static void run(Entry e) throws Exception {
        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        boolean ok = fetchAndInstall(http, e.slug(), 0, new HashSet<>());
        STATUS.put(e.slug(), ok ? "Installé - redémarre" : "Indisponible en 1.21.11");
    }

    private static boolean fetchAndInstall(HttpClient http, String project, int depth, Set<String> seen) throws Exception {
        if (!seen.add(project) || depth > 3) return true;
        if (project.equals("P7dR8mSH")) return true; // Fabric API : déjà requis par 802 Client
        String games = URLEncoder.encode("[\"1.21.11\"]", StandardCharsets.UTF_8);
        String loaders = URLEncoder.encode("[\"fabric\"]", StandardCharsets.UTF_8);
        String url = "https://api.modrinth.com/v2/project/" + project + "/version?game_versions=" + games + "&loaders=" + loaders;
        HttpResponse<String> res = http.send(HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA).GET().build(),
            HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) return false;
        JsonArray versions = JsonParser.parseString(res.body()).getAsJsonArray();
        if (versions.isEmpty()) return false;
        JsonObject version = versions.get(0).getAsJsonObject();
        for (JsonElement el : versions) {
            JsonObject v = el.getAsJsonObject();
            if ("release".equals(v.get("version_type").getAsString())) {
                version = v;
                break;
            }
        }
        if (version.has("dependencies")) {
            for (JsonElement el : version.getAsJsonArray("dependencies")) {
                JsonObject d = el.getAsJsonObject();
                if (d.has("dependency_type") && "required".equals(d.get("dependency_type").getAsString())
                    && d.has("project_id") && !d.get("project_id").isJsonNull()) {
                    if (!fetchAndInstall(http, d.get("project_id").getAsString(), depth + 1, seen)) return false;
                }
            }
        }
        JsonArray files = version.getAsJsonArray("files");
        JsonObject file = files.get(0).getAsJsonObject();
        for (JsonElement el : files) {
            JsonObject f = el.getAsJsonObject();
            if (f.has("primary") && f.get("primary").getAsBoolean()) {
                file = f;
                break;
            }
        }
        String fileUrl = file.get("url").getAsString();
        String filename = file.get("filename").getAsString();
        String sha512 = file.getAsJsonObject("hashes").get("sha512").getAsString();
        if (!fileUrl.startsWith("https://cdn.modrinth.com/") || !filename.endsWith(".jar")
            || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return false;
        }
        Path mods = FabricLoader.getInstance().getGameDir().resolve("mods");
        Files.createDirectories(mods);
        if (Files.exists(mods.resolve(filename))) return true;
        byte[] data = http.send(HttpRequest.newBuilder(URI.create(fileUrl)).header("User-Agent", UA).GET().build(),
            HttpResponse.BodyHandlers.ofByteArray()).body();
        StringBuilder hex = new StringBuilder();
        for (byte b : MessageDigest.getInstance("SHA-512").digest(data)) hex.append(String.format("%02x", b));
        if (!hex.toString().equalsIgnoreCase(sha512)) return false;
        Files.write(mods.resolve(filename), data);
        return true;
    }
}
