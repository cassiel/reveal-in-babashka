(ns net.cassiel.reveal-bb.watcher
  (:require [babashka.fs :as fs]
            [pod.babashka.fswatcher :as fw]
            [clojure.java.shell :refer [sh]]))

(defn watch [clj-file]
  (letfn [(process []
            (println (str (java.util.Date.)) "--" clj-file)
            (sh "bb" clj-file))]
    (process)
    (fw/watch clj-file
              (fn [event]
                ;; (println event)
                (when (#{:write :write|chmod} (:type event))
                  (process)))
              {:delay-ms 250})))

(defn watch-all [& clj-files]
  (doseq [f clj-files] (watch f))
  (deref (promise)))
