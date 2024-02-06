(ns net.cassiel.reveal-bb.render
  (:require [babashka.pods :as pods]
            [babashka.fs :as fs]))

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
                   (fs/file "images")
                   str)}])

(def image (partial image-h 480))

(defn include [f & {:keys [lines]}]
  (let [content (-> (slurp (fs/file "include" f))
                    #_ (htmlize))]
    (let [attrs {:data-trim 1}
          attrs (if lines (assoc attrs :data-line-numbers lines) attrs)]
      [:pre [:code attrs [:script {:type "text/template"} content]]])
    ))

(defn- copy-reveal-js [reveal-location out-dir]
  (when (fs/exists? out-dir) (fs/delete-tree out-dir))
  (fs/create-dir out-dir)
  (doseq [f ["dist" "plugin"]]
    (fs/copy-tree (fs/file reveal-location f)
                  (fs/file out-dir f))))

(defn render [& {:keys [theme title author slides reveal-location css out-dir]
                 :or {css "local-style.css"
                      out-dir "."}}]
  (let [template (clojure.java.io/resource "template.html")
        global-css "global-style.css"
        global-style (clojure.java.io/resource global-css)
        template-html (slurp template)
        content (utils/as-html slides)
        reveal-location (fs/expand-home reveal-location)
        out-dir (fs/expand-home out-dir)
        all-html (-> template-html
                     (clojure.string/replace "__TITLE__" title)
                     (clojure.string/replace "__AUTHOR__" author)
                     (clojure.string/replace "__THEME__" (name theme))
                     (clojure.string/replace "__CONTENT__" content))
        out-dir (fs/file out-dir "_OUTPUT")]
    (copy-reveal-js (fs/expand-home reveal-location) out-dir)

    (fs/copy (clojure.java.io/resource global-css) (fs/file out-dir global-css))

    (when (fs/exists? css) (fs/copy css (fs/file out-dir "local-style.css")))

    (let [img "images"]
      (when (fs/exists? img) (fs/copy-tree img (fs/file out-dir img))))

    (spit (fs/file out-dir "index.html") all-html)))
