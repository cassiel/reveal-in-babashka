(ns net.cassiel.reveal-bb.render
  (:require [babashka.pods :as pods]
            [babashka.fs :as fs])
  (:import [java.io File]))

(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")
(require '[pod.retrogradeorbit.bootleg.utils :as utils])

(defn htmlize [text]
  (-> text
      (clojure.string/replace "&" "&amp;")
      (clojure.string/replace "<" "&lt;")
      (clojure.string/replace ">" "&gt;")))

(defn tt [text]
  [:span.tt (htmlize text)])

(defn link
  "Raw link, URL monospaced - or link with text."
  ([url content] [:a {:href url} content])
  ([url] (link url [:code url])))

(defn image-h [h f]
  [:img {:height h
         :src (->> f
                   (File. "images")
                   #_ (File. ASSET-ROOT-URL)
                   str)}])

(def image (partial image-h 480))

(defn include [f & {:keys [lines]}]
  (let [content (-> (slurp (File. "include" f))
                    #_ (htmlize))]
    (let [attrs {:data-trim 1}
          attrs (if lines (assoc attrs :data-line-numbers lines) attrs)]
      [:pre [:code attrs [:script {:type "text/template"} content]]])
    ))

(defn- copy-reveal-js [reveal-location out-dir]
  (when (fs/exists? out-dir) (fs/delete-tree out-dir))
  (fs/create-dir out-dir)
  (doseq [f ["dist" "plugin"]]
    (fs/copy-tree (File. reveal-location f)
                  (File. out-dir f))))

(defn render [& {:keys [theme title author slides reveal-location out-dir]
                 :or {out-dir "."}}]
  (let [template (clojure.java.io/resource "template.html")

        global-style-name "global-style.css"

        global-style (clojure.java.io/resource global-style-name)
        template-html (slurp template)
        content (utils/as-html slides)
        all-html (-> template-html
                     (clojure.string/replace "__TITLE__" title)
                     (clojure.string/replace "__AUTHOR__" author)
                     (clojure.string/replace "__THEME__" (name theme))
                     (clojure.string/replace "__CONTENT__" content))
        out-dir (File. out-dir "_OUTPUT")]
    (copy-reveal-js reveal-location out-dir)

    (let [g-css "global-style.css"]
      (fs/copy (clojure.java.io/resource g-css) (File. out-dir g-css)))

    (let [l-css "local-style.css"]
      (when (fs/exists? l-css) (fs/copy l-css (File. out-dir l-css))))

    (let [img "images"]
      (when (fs/exists? img) (fs/copy-tree img (File. out-dir img))))

    (spit (File. out-dir "index.html") all-html)))
