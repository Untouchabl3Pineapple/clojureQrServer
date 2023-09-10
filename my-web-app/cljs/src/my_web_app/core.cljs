(ns my-web-app.core)

(def video (.getElementById js/document "qr-video"))

(def qrResult (.getElementById js/document "qr-result"))

(defn tick
  []
  (when (= (.-readyState video) (.-HAVE_ENOUGH_DATA video))
    (set! (.-textContent qrResult) "Scanning...")
    (def canvasElement (.createElement js/document "canvas"))
    (def canvas (.getContext canvasElement "2d"))
    (set! (.-width canvasElement) (.-videoWidth video))
    (set! (.-height canvasElement) (.-videoHeight video))
    (.drawImage
     canvas
     video
     0
     0
     (.-width canvasElement)
     (.-height canvasElement))
    (def imageData
      (.getImageData
       canvas
       0
       0
       (.-width canvasElement)
       (.-height canvasElement)))
    (def code
      (js/jsQR
       (.-data imageData)
       (.-width imageData)
       (.-height imageData)
       #js {:inversionAttempts "dontInvert"}))
    (if code
      (set! (.-textContent qrResult) (str "" (.-data code)))
      (set! (.-textContent qrResult) "QR code not found")))
  (js/requestAnimationFrame tick))

(.then
 (.getUserMedia
  (.-mediaDevices js/navigator)
  #js {:video #js {:facingMode "environment"}})
 (fn [stream]
   (set! (.-srcObject video) stream)
   (.setAttribute video "playsinline" true)
   (.play video)
   (js/requestAnimationFrame tick)))

