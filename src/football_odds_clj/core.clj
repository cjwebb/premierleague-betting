(ns football-odds-clj.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:gen-class))

;HomeTeam = Home Team
;AwayTeam = Away Team
;FTHG = Full Time Home Team Goals
;FTAG = Full Time Away Team Goals
;B365H = Bet365 home win odds
;B365D = Bet365 draw odds
;B365A = Bet365 away win odds

(def season-09-10
  "/Users/colin/Downloads/09-10.csv")

(defn read-csv
  "Reads CSV, and returns a list of data"
  [filename]
  (with-open [in-file (io/reader filename)]
    (doall
      (csv/read-csv in-file))))

(defn headers
  "Reads the first line of a CSV"
  [csv-filename]
  (first (read-csv csv-filename)))

(defn header-indices
  "Returns a map with keys as the column headers,
  and the value as the index of the column header in the csv:

  => {\"HomeTeam\" 2 \"Away Team\" 3} etc.."
  [csv-filename]
  (into {}
        (map-indexed (fn [index item] {item index})
                     (headers csv-filename))))

(defn extract-data
  "Transforms vector of CSV line into a map, where the keys are retrieved from
  header-indices map.

  => {\"HomeTeam\" \"Aston Villa\" \"AwayTeam\" \"Wigan\"}"
  [header-indices line]
  (let [date          (get line (get header-indices "Date"))
        home-team     (get line (get header-indices "HomeTeam"))
        away-team     (get line (get header-indices "AwayTeam"))
        home-score    (get line (get header-indices "FTHG"))
        away-score    (get line (get header-indices "FTAG"))
        home-win-odds (get line (get header-indices "B365H"))
        draw-odds     (get line (get header-indices "B365D"))
        away-win-odds (get line (get header-indices "B365A"))]
    {:date date
     :home-team home-team
     :away-team away-team
     :home-score home-score
     :away-score away-score
     :home-win-odds home-win-odds
     :draw-odds draw-odds
     :away-win-odds away-win-odds}))

(defn -main
  "Prints out certain game data from csv"
  [& args]
  (let [filename season-09-10
        header-idx (header-indices filename)
        games (drop 1 (read-csv filename))]
    (doall
      (->> (map #(extract-data header-idx %) games)
           (map println)))))
