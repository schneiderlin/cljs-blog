(ns cljs.user
  "Commonly used symbols for easy access in the ClojureScript REPL during
  development."
  (:require
   [cljs.repl :refer (Error->map apropos dir doc error->str ex-str ex-triage
                                 find-doc print-doc pst source)]
   [clojure.pprint :refer (pprint)]
   [clojure.string :as str]
   #_[nextjournal.clerk :as clerk]))

#_(defn mycatchf [code]
  (try
    (eval code)
    (catch Exception e
      (prn "caught" e))))

#_(clerk/serve! {:browse? true
               :watch-paths ["/Users/linzihao/Desktop/workspace/blog/cljs-blog/src/cljs_blog/notebook"]})
