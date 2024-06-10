# Welcome

A project for reading novel, comic

-   Crawl data from [Tang Thu Vien](https://truyen.tangthuvien.vn/), [Truyen Chu TH](https://truyenchuth.com/)
-   Crawl data + API from [Truyen Full](https://truyenfull.vn/) and [API Truyen Full](https://www.postman.com/apptimviec/workspace/app-truyenfull-vn/documentation/1352944-a47fadc6-15df-4c8a-a26b-798586316a6d)

-   Html to PDF API: [PDF from HTML](https://developer.pdf.co/api/pdf-from-html/index.html)

---

## Api document: [API Document using Swagger](http://localhost:8080/swagger-ui/index.html)

## Front-end

### Prerequisite

-   npm, npx
-   NodeJs

### Technical Stacks

-   React
-   TailwindCss

## Back-end

### Prerequisite

-   JDK 17+
-   Maven 3.5+
-   IDE: IntelliJ, Eclipse or NetBean, ...
-   Alternate IDE: VS Code with **Spring Boot Extension Pack** extension.

### Technical Stacks

-   Java 17
-   Spring Boot 3.2.4
-   Maven 3.5+

### Dependencies

-   Lombok
-   JSoup v1.15.3
-   Gson v2.10.1
-   Swagger MVC UI v2.5.0
-   OkHttp v4.10.0
-   HttpClient

---

## Others

-   SonarQube
    -   How to install:
        -   Prerequisites: Docker, Maven
        *   For Back-end
        1. Pull the SonarQube image from Docker Hub: **docker pull sonarqube:lst-community**
        2. Run the image at the port 9000: **docker run --name=docker-sonarqube -p=9000:9000 -d sonarqube:lts-community**
        3. Open localhost:9000 to login, default username and password is "admin"
        4. At backend/comic folder, run this command in the terminal : **mvn sonar:sonar -D sonar.login=your_username -D sonar.password=your_password**
        5. Wait until it done, then open this link: **http://localhost:9000/dashboard?id=com.group17%3Acomic**
