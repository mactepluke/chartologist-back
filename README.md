```mermaid
erDiagram

    USER {
        USER_ID INT[PK]
        EMAIL VARCHAR[50]
        PASSWORD VARCHAR[30]
        FIRST_NAME VARCHAR[30]
        LAST_NAME VARCHAR[30]
        VERIFIED BOOLEAN
        ENABLED BOOLEAN
}

```