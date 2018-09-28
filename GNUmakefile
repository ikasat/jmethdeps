.PHONY: build install clean

DIST_TAR := build/distributions/jmethdeps.tar
INSTALL_PREFIX := $(HOME)/.local/opt

build:
	./gradlew build

install: build
	mkdir -p $(INSTALL_PREFIX)
	tar xvf $(DIST_TAR) -C $(INSTALL_PREFIX)

clean:
	./gradlew clean
