%global commit0 981d4704b8bc7dab917141d1d7c77b07361223f7
%global shortcommit0 %(c=%{commit0}; echo ${c:0:7})
%define __jar_repack %{nil}

Name:           mtsar
Version:        0.0.0
Release:        1%{?dist}
Summary:        Mechanical Tsar
License:        APL 2.0
URL:            http://mtsar.nlpub.org/
Source0:        https://github.com/mtsar/%{name}/archive/%{commit0}.tar.gz#/%{name}-%{shortcommit0}.tar.gz
BuildArch:      noarch
BuildRequires:  maven

%description
Mechanical Tsar is an engine for mechanized labor workflows.

%prep
%autosetup -n %{name}-%{commit0}

%build
mvn -T 4 -B package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

%install
mkdir -p %{buildroot}%{_javadir}
install -p -m 644 target/mtsar-*.jar %{buildroot}%{_javadir}/mtsar.jar

%files
%{_javadir}/mtsar.jar
