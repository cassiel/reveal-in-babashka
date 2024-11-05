(ns net.cassiel.reveal-bb.render
  (:require [babashka.fs :as fs]
            [pod.retrogradeorbit.bootleg.utils :as utils]))

(defn htmlize [text]
  (-> text
      (clojure.string/replace "&" "&amp;")
      (clojure.string/replace "<" "&lt;")
      (clojure.string/replace ">" "&gt;")))

(defn ^:deprecated tt [text]
  ;; TODO: should this allow multiple items (like quotes, below)?
  [:span.tt (htmlize text)])

(defn element
  "Build a Hiccup element. Useful for building elements from iteration
   over a sequence, and/or using a `#(...)` lambda."
  [tag items]
  (vec (cons tag items)))

(defn- quote [lq rq & items]
  (as-> items X
    (interpose " " X)
    (cons lq X)
    (cons :span X)
    (vec X)
    (conj X rq))
  )

;; TODO: do we care about smart quotes in apostrophes?

(defn squote [& items] (apply quote "&lsquo;" "&lsquo;" items))
(defn dquote [& items] (apply quote "&ldquo;" "&rdquo;" items))

;; `style` places ";" as separator, not terminator, so might not be 100% legit.
(defn style [items]
  {:style
   (reduce (fn [result [k v]]
             (let [kv (str (name k) ": " v)]
               (if result
                 (str result "; " kv)
                 kv)))
           nil
           items)})

(defn link
  "Raw link, URL monospaced - or link with text."
  ([url content] [:a {:href url} content])
  ([url] (link url [:code.link url])))

(defn ^:deprecated image-h [h f]
  [:img {:height h
         :src (->> f
                   (fs/file "images")
                   str)}])

(defn image [f & {:keys [h] :or {h 480}}]
  [:img {:height h
         :src (->> f
                   (fs/file "images")
                   str)}])

(defn include
  "Include some code. Unlike images/assets, this is done in the renderer, not by
   the final web server or viewer."
  [f & {:keys [h w lines scale]
        :or {h 400 w 800 scale 1.0}}]
  (let [input-location  (System/getenv "INPUT_LOCATION")        ; TODO: can't we just rebind instead?
        content (-> (slurp (fs/file input-location "include" f))
                    #_ (htmlize))]
    (let [attrs {:data-trim 1
                 :width "100%"
                 ;; :style "height: 500px"
                 }
          attrs (if lines (assoc attrs :data-line-numbers lines) attrs)]
      [:pre (style {:height (format "%dpx" h)
                    :width (format "%dpx" w)
                    :font-size (format "%.2fem" (* scale 0.5))}) [:code attrs [:script {:type "text/template"} content]]])
    ))

(defn- copy-reveal-js [reveal-location out-dir]
  (when (fs/exists? out-dir) (fs/delete-tree out-dir))
  (fs/create-dirs out-dir)
  (doseq [f ["dist" "plugin"]]
    (fs/copy-tree (fs/file reveal-location f)
                  (fs/file out-dir f))))

(defn render [& {:keys [theme title author slides css]
                 :or   {css "local-style.css"}}]
  (let [input-location  (System/getenv "INPUT_LOCATION")
        input-file      (System/getenv "INPUT_FILE")
        input-slug      (fs/strip-ext input-file)
        reveal-location (System/getenv "REVEAL_LOCATION")
        _               (when false
                          (println ">> REVEAL_LOCATION:" reveal-location)
                          (println ">> INPUT_LOCATION: " input-location)
                          (println ">> INPUT_FILE:     " input-file)
                          (println ">> input_slug:     " input-slug))
        template        (clojure.java.io/resource "template.html")
        global-css      "global-style.css"
        global-style    (clojure.java.io/resource global-css)
        template-html   (slurp template)
        content         (utils/as-html slides)
        reveal-location (fs/expand-home reveal-location)
        out-dir         (fs/file input-location "_OUTPUT" input-slug)
        css             (fs/file input-location css)
        all-html        (-> template-html
                            (clojure.string/replace "__TITLE__" title)
                            (clojure.string/replace "__AUTHOR__" author)
                            (clojure.string/replace "__THEME__" (name theme))
                            (clojure.string/replace "__CONTENT__" content))
        ;; TODO: out-dir should be alongside the input file, with a unique name.
        ]
    (copy-reveal-js (fs/expand-home reveal-location) out-dir)

    (fs/copy (clojure.java.io/resource global-css)
             (fs/file out-dir global-css))

    (when (fs/exists? css) (fs/copy css (fs/file out-dir "local-style.css")))

    (doseq [dir ["images" "assets" "md"]]
      (let [full-dir (fs/file input-location dir)]
        (when (fs/exists? full-dir) (fs/copy-tree full-dir (fs/file out-dir dir)))))

    (spit (fs/file out-dir "index.html") all-html)))
