
build:
	javac *.java

noreverse:
	java Router routerA.txt
	java Router routerB.txt

clean:
	rm -f *.class