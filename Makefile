JAR := tCHu.jar

run:
	java -jar $(JAR)

merge_split_jar:
	cat ./archived-jar-torun/*.zip > tmp.zip && unzip tmp.zip && mv ./CS108_Project-tCHu.jar $(JAR); rm tmp.zip

clean:
	@-rm *.jar *.zip &> /dev/null 
