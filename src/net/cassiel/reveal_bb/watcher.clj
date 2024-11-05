(ns net.cassiel.reveal-bb.watcher
  (:require [babashka.fs :as fs]
            [pod.babashka.fswatcher :as fw]
            [clojure.java.shell :refer [sh]]
            [clojure.core.async :as a]))

(defn watch
  "Run watcher on a Clojure file of slides.
  `reveal-location` is the root of our checked-out `reveal.js`, `ssi-version` branch."
  [reveal-location clj-file]
  (letfn [(process []
            (println (str (java.util.Date.)) "--" clj-file)
            (let [output (sh "bb" clj-file :env {:INPUT_LOCATION (-> (fs/absolutize clj-file) (fs/parent))
                                                 :INPUT_FILE (fs/file-name clj-file)
                                                 :REVEAL_LOCATION reveal-location})]
              (when-let [out (:out output)]
                (when (pos? (count out))
                  (println "OUT:")
                  (println out)))
              (when-let [err (:err output)]
                (when (pos? (count err))
                  (println "ERR:")
                  (println err)))))]
    (process)
    (fw/watch clj-file
              (fn [event]
                ;; (println "EVENT" event)
                ;; Sometimes only get :chmod; often get it after another type.
                (when (#{:write :write|chmod :chmod} (:type event))
                  (process)))
              {:delay-ms 500})))

(defn watch-all [reveal-location filespec]
  (doseq [path (fs/glob "." filespec)
          :let [name (str path)]]
    (if (re-matches #".*\.clj$" name)
      (watch reveal-location name)
      (println "ignoring" name)))
  (a/go-loop []
    (println (str (java.util.Date.)) "...")
    (a/<! (a/timeout 5000))
    (recur)
    )
  (deref (promise)))
