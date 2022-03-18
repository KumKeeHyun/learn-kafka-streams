kafkacat -b :9092 -K "|" -t products -P -l products.json
kafkacat -b :9092 -K "|" -t players -P -l players.json
kafkacat -b :9092 -K "|" -t score-events -P -l score-events.json
