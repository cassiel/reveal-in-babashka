(require '[babashka.pods :as pods])
(require '[babashka.cli :as cli])

(def cli-opts
  {:reveal-loc {:alias :r
                :desc "Location of reveal.js directory"
                :require true}
   :document-loc {:alias :d
                  :desc "Location of document index.clj, destination for render"
                  :require true}}
  )


(slurp "/Users/nick/GITHUB/cassiel/reveal.js/index.html")




(-> (slurp "/Users/nick/GITHUB/cassiel/reveal.js/index.html")
    (utils/convert-to :html)
    )

(require '[sci.core :as cli])

(sci/eval-string "(inc 1)")
