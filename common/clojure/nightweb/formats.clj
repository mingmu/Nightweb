(ns nightweb.formats)

(defn b-encode
  [data-map]
  (org.klomp.snark.bencode.BEncoder/bencode data-map))

(defn b-decode
  [data-barray]
  (try
    (.getMap (org.klomp.snark.bencode.BDecoder/bdecode
               (java.io.ByteArrayInputStream. data-barray)))
    (catch java.lang.Exception e nil)))

(defn b-decode-list
  [be-value]
  (try
    (.getList be-value)
    (catch java.lang.Exception e nil)))

(defn b-decode-bytes
  [be-value]
  (try
    (.getBytes be-value)
    (catch java.lang.Exception e nil)))

(defn b-decode-long
  [be-value]
  (try
    (.getLong be-value)
    (catch java.lang.Exception e nil)))

(defn b-decode-string
  [be-value]
  (try
    (.getString be-value)
    (catch java.lang.Exception e nil)))

(defn base32-encode
  [data-barray]
  (if data-barray
    (net.i2p.data.Base32/encode data-barray)))

(defn base32-decode
  [data-str]
  (if data-str
    (net.i2p.data.Base32/decode data-str)))

(defn long-decode
  [data-str]
  (try
    (Long/parseLong data-str)
    (catch java.lang.Exception e nil)))

(defn url-encode
  [content]
  (let [params (concat
                 (if-let [type-val (get content :type)]
                   [(str "type=" (name type-val))])
                 (if-let [hash-val (get content :hash)]
                   [(str "hash=" (base32-encode hash-val))])
                 (if-let [userhash-val (get content :userhash)]
                   [(str "userhash=" (base32-encode userhash-val))])
                 (if-let [time-val (get content :time)]
                   [(str "time=" time-val)]))]
    (str "http://nightweb.net#" (clojure.string/join "&" params))))

(defn url-decode
  [url]
  (let [url-str (subs url (+ 1 (.indexOf url "#")))
        url-vec (clojure.string/split url-str #"[&=]")
        url-map (if (even? (count url-vec))
                  (apply hash-map url-vec)
                  {})
        {type-val "type"
         hash-val "hash"
         userhash-val "userhash"
         time-val "time"} url-map]
    {:type (if type-val (keyword type-val))
     :hash (if hash-val (base32-decode hash-val))
     :userhash (if userhash-val (base32-decode userhash-val))
     :time (if time-val (long-decode time-val))}))
