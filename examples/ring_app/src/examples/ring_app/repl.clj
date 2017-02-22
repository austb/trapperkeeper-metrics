(ns examples.ring-app.repl
  (:require
    [puppetlabs.trapperkeeper.core :as tk]
    [puppetlabs.trapperkeeper.app :as tka]
    [puppetlabs.trapperkeeper.bootstrap :as bootstrap]
    [puppetlabs.trapperkeeper.config :as config]
    [clojure.tools.namespace.repl :refer (refresh)]))

;; This namespace shows an example of the "reloaded" clojure workflow
;; ( http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded )
;;
;; It's based on the pattern from Stuart Sierra's `Component` library:
;; ( https://github.com/stuartsierra/component#reloading )
;;
;; You can load this namespace up into a REPL and then run `(go)` to boot
;; and run the sample application.  Then, you can run `(reset)` at any time
;; to stop the running app, reload all of the necessary namespaces, and start
;; a new instance of the app.  This means that you can do iterative development
;; without having to restart the whole JVM.
;;
;; You can also view the context of the application (and all of the
;; trapperkeeper services) via `(context)` (or pretty-printed with
;; `print-context`).

(def system nil)

(defn init []
  (alter-var-root #'system
                  (fn [_] (tk/build-app
                           (bootstrap/parse-bootstrap-config!
                            "./examples/ring_app/bootstrap.cfg")
                           (config/load-config "./examples/ring_app/ring-example.conf"))))
  (alter-var-root #'system tka/init)
  (tka/check-for-errors! system))

(defn start []
  (alter-var-root #'system
                  (fn [s] (if s (tka/start s))))
  (tka/check-for-errors! system))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (if s (tka/stop s)))))

(defn go []
  (init)
  (start))

(defn context []
  @(tka/app-context system))

(defn print-context []
  (clojure.pprint/pprint (context)))

(defn reset []
  (stop)
  (refresh :after 'examples.ring-app.repl/go))
