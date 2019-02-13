/*
--********** BUSCAR PROXIMO CODIGO NA SEQUENCIA **********

-- Para testar: 

CREATE TABLE dbo.teste (Codigo VARCHAR(7))

INSERT INTO teste VALUES (0000001)
INSERT INTO teste VALUES (0000003)
INSERT INTO teste VALUES (0000004)
INSERT INTO teste VALUES (0000002)

DELETE FROM teste WHERE Codigo='1'

*/

SELECT * FROM teste

SELECT TOP 1 * FROM (
    SELECT t1.Codigo+1 AS Id
    FROM teste t1
    WHERE NOT EXISTS(SELECT * FROM teste t2 WHERE t2.Codigo = t1.Codigo + 1 )
    UNION 
    SELECT 1 AS Id
    WHERE NOT EXISTS (SELECT * FROM teste t3 WHERE t3.Codigo = 1)) ot
ORDER BY 1
