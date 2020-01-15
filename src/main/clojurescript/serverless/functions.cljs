(ns serverless.functions
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs-http.client :as http]
            [cljs.core.async :refer [put! chan <!]]))

(nodejs/enable-util-print!)
(defonce moment (nodejs/require "moment"))

(defn hello [event ctx cb]
  (println ctx)
  (cb nil (clj->js
           {:statusCode 200
            :headers    {"Content-Type" "text/html"}
            :body       "<h1>Hello, World!</h1>"})))


(defn now [event ctx cb]
  (println ctx)
  (cb nil (clj->js
           {:statusCode 200
            :headers    {"Content-Type" "text/html"}
            :body       (str "<h1>" (.format (moment.) "LLLL") "</h1>")}))) ; call nodejs package

(defn example [event ctx cb]
  (go (let [response (<! (http/get "https://sv443.net/jokeapi/category/Programming"))
            the-glorious-data  (->> response
                                  (:body)
                                  (clj->js)
                                  (.stringify js/JSON))]
        (cb nil (clj->js
                 {:statusCode 200
                  :headers    {"Content-Type" "application/json"}
                  :body       the-glorious-data})))))

(set! (.-exports js/module) #js
                             {:hello hello
                              :now now
                              :example example})