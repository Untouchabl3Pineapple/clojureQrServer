(ns my-web-app.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [com.nopolabs.clozxing.encode :as encode]
            [com.nopolabs.clozxing.decode :as decode]
            [hiccup.core :refer :all]
            [ring.util.response :as response]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [response]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [my-web-app.pages :as pages]
            [my-web-app.responses :as responses]
            [my-web-app.cas :as cas]))

;; TODO: make a multi-file project

(def db-params
  {:dbtype   "postgresql"
   :dbname   "postgres"
   :user     "postgres"
   :password "12345"
   :host     "localhost"
   :port     5432})

(defn get-connection []
  (jdbc/get-datasource db-params))



;(def global-token (atom "string")) мб вернуть



(defn get-all-logs [db]
  (jdbc/execute! db ["SELECT * FROM logger ORDER BY log_date_time DESC"] {:builder-fn rs/as-unqualified-lower-maps}))

(defn logger-handler [req]
  (if (cas/verify-token (str (deref cas/global-token))) 
     (let [db (get-connection)
           logs (get-all-logs db)]
       (pages/logger-page logs)
       ) 
    (responses/illegal-token)
    )
)
  


(defn qr-generator [text]
  (if (empty? text)
    (pages/qr-page)
    (let [unique-filename (str (java.util.UUID/randomUUID) ".png")
          file-path (str "resources/public/qrcodes/" unique-filename)]
      (encode/to-file
       text
       file-path
       {:size 200
        :error-correction encode/error-correction-H
        :character-set encode/iso-8859-1
        :margin 1
        :format "PNG"})

      (pages/qr-generator-page unique-filename text))))



(defn home-handler [request]
  (pages/home-page)
  )

(defn login-handler [request] 
  (if (= 1 1) 
     (cas/reset-all)
    
    )
  (pages/layout (pages/login-page)))

(defn logout-handler [request]
  (if (= 1 1)
    (cas/reset-all))
  (responses/redirect "/home" 302))


(defroutes app-routes
  (GET "/home" [] home-handler)
  (GET "/scanner" [] (pages/scanner-page))
  (GET "/logger" [] logger-handler)
  (GET "/login" [] login-handler)
  (GET "/logout" [] logout-handler)
  (POST "/login" [] cas/cas-auth)

  (POST "/hi" [qrData] (str qrData))

  (GET "/qrgenerator" [] (pages/qr-page))
  (POST "/qrgenerator" [text] (qr-generator text)))


(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})))

