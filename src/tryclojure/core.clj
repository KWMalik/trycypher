(ns tryclojure.core
  (:use ring.adapter.jetty
	net.licenser.sandbox
	[net.licenser.sandbox tester matcher]
	[hiccup core form-helpers page-helpers]
	[ring.middleware reload stacktrace params file session]
	net.cgrand.moustache
	[clojure.stacktrace :only [root-cause]]
	tryclojure.tutorial)
  (:import java.io.StringWriter
	   org.apache.commons.lang.StringEscapeUtils
	   java.util.concurrent.TimeoutException
	   java.net.URLDecoder))

(def sandbox-tester
     (extend-tester secure-tester 
		    (whitelist 
		     (function-matcher 'println 'print 'pr 'prn 'var 'print-doc 'doc 'throw 'def 'def*))))

(def state-tester
     (new-tester (whitelist (constantly '(true))) ; we want everything to be true
		 (blacklist (function-matcher 'def 'def*))))

(defn has-state? [form]
     (not (state-tester form nil)))
		 

(defn execute-text [txt history]
  (let [
	sc (new-sandbox-compiler :tester sandbox-tester 
				 :timeout 1000)
	form (binding [*read-eval* false] (read-string txt))
	result (try
		(loop [history history]
		  (if (not (empty? history))
		    (do
		      (print "Executing history:")
		      (prn (first history))
		      ((sc (first history)))
		      (recur (next history)))))
		(with-open [writer (java.io.StringWriter.)]
		  (let [r (pr-str ((sc form) {'*out* writer}))]
		    [(str writer r)
		     (if (has-state? form) (conj history form) history)]))
		(catch TimeoutException _ ["Execution Timed Out!" history])
		(catch SecurityException _ ["Disabled for security purposes."  history])
		(catch Exception e [(str (root-cause e))  history]))]
    result))

(def links
     (html (unordered-list 
	    [(link-to "http://clojure.org" "The official Clojure website")
	     (link-to "http://www.assembla.com/wiki/show/clojure/Getting_Started" "Getting started with Clojure")
	     (link-to "http://groups.google.com/group/clojure" "Clojure mailing list")
	     (link-to "http://java.ociweb.com/mark/clojure/article.html" "A comprehensive Clojure tutorial")
	     (link-to "http://joyofclojure.com/" "The Joy of Clojure: a book by Michael Fogus and Chris Houser")
	     (link-to "http://www.pragprog.com/titles/shcloj/programming-clojure" "Programming Clojure, a book by Stuart Halloway")
	     (link-to "http://disclojure.org" "Disclojure")
	     (link-to "http://planet.clojure.in" "Planet Clojure")])))

(def bottom-html
     (html [:p.bottom
	    "This site is still under construction. I can't promise everything will work correctly."
	    " You can find the site's source and such on it's " (link-to "http://github.com/Raynes/tryclojure" "github")
	    " page."
	    [:br] [:br]
	    "TryClojure is written in Clojure and JavaScript, powered by " 
	    (link-to "http://github.com/Licenser/clj-sandbox" "clj-sandbox")
	    " and Chris Done's "
	    (link-to "http://github.com/chrisdone/jquery-console" "jquery-console") ""
	    [:br] [:br]
	    "Huge thanks to " (link-to "http://www.bestinclass.dk/" "Lau Jensen")
	    " for lots of help with everything ranging from Gimp, to straight up CSS and HTML design tips."]))

(def fire-html
     (html
      (:html4 doctype)
      [:head
       (include-css "/resources/public/css/tryclojure.css")
       (include-js "/resources/public/javascript/jquery-1.4.2.min.js"
		   "/resources/public/javascript/jquery.console.js"
		   "/resources/public/javascript/tryclojure.js")
       [:title "TryClojure"]]
      [:body
       [:div {:style "text-align: center;"} [:h1 "Try Clojure"]]
       [:div#container [:div#console.console]
	[:table.bottom {:border "0"}
	 [:tr]
	 [:td.bholder [:div.buttons 
		       [:a#tutorial.buttons "tutorial"]
		       [:a#links.buttons "links"]
		       [:a#about.lbutton "about"]]]
	 [:tr]
	 [:td [:div#changer "omg"]]]]
       [:div.footer [:p.footer "Copyright 2010 Anthony Simpson. All Rights Reserved."]]]))

(defn handler [{session :session}]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :session session
   :body    fire-html})

(defn div-handler [{qparams :query-params session :session}]
  (print "qparams: ") (prn qparams)
  (print "session: ") (prn session)
  (let [[result history] (execute-text (qparams "code") (or (:history session) [])) ]
    (print "result: ") (prn result)
    (print "history: ") (prn history)
    {:status  200
     :headers {"Content-Type" "text/html"}
     :session {:history history}
     :body    result}))

(defn about-handler [{session :session}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body bottom-html})

(defn link-handler [{session :session}]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body links})

(defn tutorial-handler [{querys :query-params session :session}]
  (println (querys "step"))
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session session
   :body (get-tutorial (querys "step"))})

(def clojureroutes
     (app
      (wrap-session)
      ;(wrap-reload '(tryclojure.core tryclojure.tutorial))
      (wrap-file (System/getProperty "user.dir"))
      (wrap-params)
      (wrap-stacktrace)
      ["tutorial"] tutorial-handler
      ["links"] link-handler
      ["about"] about-handler
      ["magics"] (wrap-session div-handler)
      [""] (wrap-session handler)))

(defn tryclj [] (run-jetty #'clojureroutes {:port 8801}))
