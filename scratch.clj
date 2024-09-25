(require '[babashka.fs :as fs])
(import 'java.io.File)

(Integer/toBinaryString 34)

(java.util.Date. (* 63 60 1000 1000 1000))

(File.
 (fs/expand-home
  (fs/file "~/A/B")))


(reduce (fn [result [k v]]
          (let [kv (str (name k) ": " v)]
            (if result
              (str result "; " kv)
              kv)))
        nil


        (seq
         {:color "red"
          :background-color "#F0F0F0"}))

(File. (str (fs/expand-home "~/A/B")))

(fs/file "X")

(fs/read-all-lines "/Users/nick/GITHUB/cassiel/reveal.js/index.html")

(slurp "/Users/nick/GITHUB/cassiel/reveal.js/index.html")

(System/getProperty "home")


(-> (slurp "/Users/nick/GITHUB/cassiel/reveal.js/index.html")
    (utils/convert-to :html)
    )

(require '[sci.core :as cli])

(sci/eval-string "(inc 1)")

(import 'java.io.File)

(File. "~/X")

(java.io.File. (java.io.File. "A") "B")

(clojure.java.io/resource "templatex.html")

(require '[net.cassiel.reveal-bb.render :as r])

r/render

(-> (System/getProperty "user.home")
    (File. "GITHUB")
    (File. "cassiel")
    (File. "reveal.js")
    str)

(File.
 (System/getProperty "user.home")
 "X")

(fs/cwd)

(-> "AXA"
    (clojure.string/replace "X" "Y")
    (clojure.string/replace "A" "B")
    )

(slurp "template.html")

(fs/cwd)

(fs/delete-tree (File. (str (fs/cwd)) "_OUTPUT"))
(fs/create-dir (File. (str (fs/cwd)) "_OUTPUT"))

(System/getenv "HOME")

(str (fs/file (fs/home)
              "GITHUB"
              "cassiel"
              "reveal.js"))

(shell/sh "printenv" "HELLO" :env {:HELLO 99})

(doseq [x (fs/glob "." "example/*.clj")] (println (str x)))

(re-matches #".*\.clj$" "x.clj")

(-> (fs/file "./xxxx.clj")
    (fs/parent)
    (as-> X
        (fs/file X "yyyy.clj")))

(fs/absolutize "scratch.clj")

(fnext (re-find #"(.+)\.clj" "presentation.clj"))
(fs/strip-ext "x/presentation.clj")

(fs/absolutize "x.clj")

(fs/file-name "a/b.clj")
