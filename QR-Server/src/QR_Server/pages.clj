(ns QR-Server.pages
  (:require

   [hiccup.core :refer :all]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [QR-Server.auth :as cas]))


(defn main-layout [title content]
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


       (if (cas/verify-admin-token (deref cas/global-token))
         [:li.nav-item [:a {:href "/logger"} "Logger"]])]]
     [:div.login-button-container
      [:a.login-button {:href (deref cas/global-login-route)} (deref cas/global-log-in-out)]]];(if (= (deref cas/global-log-in-out) "Logout") (do (cas/reset-all) "/home") "/login")
    content]))


(defn scanner-page []
  (main-layout "Online scanner"
               [:div.container
                (anti-forgery-field)
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/scanner.css"}]
                [:body
                 [:h1 "Show your QR code"]
                 [:video {:id "qr-video" :width 300 :height 300 :autoplay true}]
                 [:p {:id "qr-result"} "QR Code not found"]
                 [:script {:src "https://cdn.rawgit.com/cozmo/jsQR/master/dist/jsQR.js"}]
                 [:script {:src "/js/scanner.js" :type "text/javascript"}]]]))


(defn logger-page [logs]
  (main-layout "Logger"
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
                  (if (seq logs)
                    (for [log logs]
                      [:tr
                       [:td (:log_id log)]
                       [:td (:log_date_time log)]
                       [:td (:log_data log)]]))
                 ;   )
                  ]]]))


(defn qr-generator-page [unique-filename text]
  (main-layout "QR Code Page"
               [:div.container
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/qrres.css"}]
                [:h1 "QR code generated"]

                [:img {:src (str "/qrcodes/" unique-filename)
                       :alt "QR Code"}]
                [:h2 (str "Encode message: " text)]]))


(defn qr-page []
  (main-layout "QR generator"
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


(defn home-page []
  (main-layout "QR Code Encoder & Decoder"
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
                 [:a {:href "/scanner" :class "button"} "Decode QR Code"]]]))


(defn login-layout [title, content]
  (hiccup.core/html
   [:head
    [:title title]
    [:link {:rel "stylesheet"
            :type "text/css"
            :href "/css/auth.css"}]]
   [:body
    content]))


(defn login-page []
  (login-layout "Authorization"
                [:div.container
                 [:form {:method "POST" :action "/login"}
                  (anti-forgery-field)
                  (if (= 1 (deref cas/global-login-err))
                    (do
                      (reset! cas/global-login-err 0)
                      [:h3 "[!] Incorrect data, try again"]))
               ;
                  [:label {:for "login"} "Login: "]
                  [:input {:type "text" :name "login"}]
                  [:br]
                  [:label {:for "password"} "Password: "]
                  [:input {:type "password" :name "password"}]
                  [:br]
                  [:input {:type "submit" :value "login"}]]]))

