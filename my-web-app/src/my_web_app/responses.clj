(ns my-web-app.responses
  (:require

   [hiccup.core :refer :all]
   [ring.util.response :as response]))

(defn illegal-token[]
  (response/content-type (response/not-found "Illegal token ") "text/plain"))

(defn redirect[redirect-url status]
  {:status status
   :headers {"location" redirect-url}})