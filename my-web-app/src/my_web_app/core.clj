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
            [next.jdbc.result-set :as rs]))

;; TODO: make a multi-file project

(def db-params
  {:dbtype   "postgresql"
   :dbname   "postgres"
   :user     "postgres"
   :password "17009839"
   :host     "localhost"
   :port     5432})

(defn get-connection []
  (jdbc/get-datasource db-params))

(defn render-page [title content]
  (hiccup.core/html
   [:head
    [:title title]
    [:link {:rel "stylesheet"
            :type "text/css"
            :href "/css/header.css"}]]
   [:body
    [:header.header
     [:nav
      [:ul.nav-list
       [:li.nav-item [:a {:href "/home"} "Home"]]
       [:li.nav-item [:a {:href "/scanner"} "QR scanner"]]
       [:li.nav-item [:a {:href "/qrgenerator"} "QR code generator"]]
       [:li.nav-item [:a {:href "/logger"} "Logger"]]]]
     [:div.login-button-container
      [:a.login-button {:href "/login"} "Login"]]]
    [:main content]]))




(defn scanner-page []
  (render-page "Online scanner"
               [:main
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/scanner.css"}]
                [:body
                 [:h1 "Show your QR code"]
                 [:video {:id "qr-video" :width 300 :height 300 :autoplay true}]
                 [:p {:id "qr-result"} "QR Code value will appear here after scanning"]
                 [:script {:src "https://cdn.rawgit.com/cozmo/jsQR/master/dist/jsQR.js"}]
                 [:script {:src "/js/app.js" :type "text/javascript"}]]]))

(defn get-all-logs [db]
  (jdbc/execute! db ["SELECT * FROM logger ORDER BY log_date_time DESC"] {:builder-fn rs/as-unqualified-lower-maps}))

(defn logger-page []
  (def db (get-connection))
  (let [logs (get-all-logs db)]
    (render-page "Logger"
                 [:div
                  [:link {:rel "stylesheet"
                          :type "text/css"
                          :href "/css/logger.css"}]
                  [:table
                   [:thead
                    [:tr
                     [:th "ID"]
                     [:th "Date"]
                     [:th "Data"]]]
                   [:tbody
                    (for [log logs]
                      [:tr
                       [:td (:log_id log)]
                       [:td (:log_date_time log)]
                       [:td (:log_data log)]])]]])))

(defn qr-page []
  (render-page "QR generator"
               [:div.qr-generator
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/qrgen.css"}]
                [:form
                 {:action "/qrgenerator"
                  :method "POST"}
                 (anti-forgery-field)
                 [:label {:for "qr-text"} "Input message to encode"]
                 [:input {:type "text"
                          :name "text"
                          :id "qr-text"}]
                 [:button {:type "submit"} "Encode"]]]))

(defn qr-generator [text]
  (if (empty? text)
    (qr-page)
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

      (render-page "QR Code Page"
                   [:main
                    [:link {:rel "stylesheet"
                            :type "text/css"
                            :href "/css/qrres.css"}]
                    [:h1 "QR code generated"]

                    [:img {:src (str "/qrcodes/" unique-filename)
                           :alt "QR Code"}]
                    [:h2 (str "Encode message: " text)]]))))







(def global-token (atom "string"))
(defn- generate-token []
  (str "valid_token")) ; Генерируем случайный токен (str (rand-int 1000000))

(defn- login-validation [login password]
  (and (= login "admin") (= password "admin")))

(defn- cas-auth [request]
  (let [params (get-in request [:params])
        url (get-in  request [:url])
        login (get params :login)
        password (get params :password)]
       ;; Проверка логина и пароля и генерация токена (пока упрощенно)
    (if (login-validation login password)
      (let [token (generate-token)
         ;   resp (client/get "http:localhost:3000/protected" {:headers {"auth" token}})
            ]
        (reset! global-token token)
        {:status 302
         :headers {"location" "/home" "auth" token}})
      (response/content-type
       (response/response "Incorrect login")
       "text/html"))))

(defn- verify-token [token]
  (= token "valid_token")) ; Проверяем, является ли токен действительным

(defn- authenticated? [request]
  (let [token request];(let [token (get-in request [:headers "Authorization"])]
    (and token (verify-token token)))) ; Проверяем, авторизован ли пользователь

(defn- unauthorized-response []
  (response/response {:status 401
                      :headers {"Content-Type" "application/json"}
                      :body "{\"error\": \"Unauthorized\"}"})) ; Ответ в случае неавторизованного доступа




(defn home-page [request]
  (let [;params (get-in request [:query-params]
        headers (get-in request [:headers])
        token (get headers :auth);token (get params "auth")
        ]
    (if (verify-token (str (deref global-token))) ;token;authenticated? token
           ;(reset! global-token "string")
      (render-page "QR Code Encoder & Decoder"
                   [:div.container
                    [:link {:rel "stylesheet"
                            :type "text/css"
                            :href "/css/home.css"}]
                    [:h1 "Welcome to the QR Code Encoder & Decoder"]
                    [:p "Encode and decode QR codes online with ease."]
                    [:div.instructions
                     [:h2 "How to Use"]
                     [:p "To encode a message into a QR code, visit the 'Encode' page."]
                     [:p "To decode a QR code and retrieve the original message, visit the 'Decode' page."]]
                    [:div.get-started
                     [:a {:href "/qrgenerator" :class "button"} "Encode QR Code"]
                     [:a {:href "/scanner" :class "button"} "Decode QR Code"]]])
      (response/content-type (response/response (str "Illegal token "  request)) "text/plain")))
  )






(defn layout [content]
  (hiccup.core/html
   [:html
    [:head [:title "Authentication Example"]]
    [:body content]]))

(defn login-form []
  (hiccup.core/html
   [:h2 "Login"]
   [:form {:method "POST" :action "/login"}
    (anti-forgery-field)
    [:label {:for "login"} "login: "]
    [:input {:type "text" :name "login"}]
    [:br]
    [:label {:for "password"} "password: "]
    [:input {:type "password" :name "password"}]
    [:br]
    [:input {:type "submit" :value "login"}]]))

(defroutes app-routes
  (GET "/home" [] home-page)
  (GET "/scanner" [] (scanner-page))
  (GET "/logger" [] (logger-page))
  (GET "/login" [] (layout (login-form)))
  (POST "/login" [] cas-auth)
  (GET "/qrgenerator" [] (qr-page))
  (POST "/qrgenerator" [text] (qr-generator text)))


(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})))

