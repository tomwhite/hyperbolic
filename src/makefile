CLASSDIR=../classes

CLASSPATH=$(CLASSDIR)

CLASSFILES= \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/HyperbolicCanvas.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/HyperbolicImageApplet.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/HyperbolicImageSource.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/HyperbolicMath.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/Scalable.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/SimpleHyperbolicImageSource.class \
	$(CLASSDIR)/uk/co/zoo/tom/hyper/Tileable.class \

JARFILE=../hyper.jar

MANIFEST=hyper.mf

$(CLASSDIR)/%.class: %.java
	export CLASSPATH; CLASSPATH=$(CLASSPATH):. ; \
	javac -d $(CLASSDIR) $<

all: $(JARFILE)

$(JARFILE) : $(CLASSFILES)
	cd $(CLASSDIR); \
	jar cmf ../src/$(MANIFEST) $(JARFILE) uk/co/zoo/tom/hyper; \
	cd ../src

clean:
	-rm $(CLASSDIR)/uk/co/zoo/tom/hyper/*

