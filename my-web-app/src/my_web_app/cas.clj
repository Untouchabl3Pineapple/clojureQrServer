(ns my-web-app.cas
  (:require

   [my-web-app.responses :as responses]
   ;[my-web-app.core :as core]
   ))
(def global-token (atom "string"))
(def global-log-in-out (atom "login"))

(defn verify-token [token]
  (= token "valid_token")) ; Проверяем, является ли токен действительным
(defn- generate-token []
  (str "valid_token")) ; Генерируем случайный токен (str (rand-int 1000000))
(defn- login-validation [login password]
  (and (= login "admin") (= password "admin")))

(defn cas-auth [request]
  (let [params (get-in request [:params]) 
        login (get params :login)
        password (get params :password)]
       ;; Проверка логина и пароля и генерация токена (пока упрощенно)
    (if (login-validation login password)
      (let [token (generate-token)
            ]
        (reset! global-token token))))
    (if (= 1 1) ;простите
    (reset! global-log-in-out "Logout")) ;под вопросом
     (responses/redirect "/home" 302)
    
  )
  
      ;(response/content-type
       ;(response/response "Incorrect login")
       ;"text/html"))))