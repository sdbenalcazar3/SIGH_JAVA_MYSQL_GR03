-- =====================================================================
-- REPORTES_JAVA_MONSTER_GR03 - Registro de opcion REP y permiso OPL004
-- Ejecutar despues de 01_sigh_gr3.sql (y opcionalmente 02_datos_crud.sql,
-- 04_listbox_cargos.sql y 05_tabpage_empleados.sql).
-- 100 % idempotente y compatible con MariaDB 10.4 (XAMPP) y MySQL 8.
-- =====================================================================
USE SIGH_GR3;

-- ---------------------------------------------------------------------
-- 1. Registro de la opcion REP (Reportes) en SEOPC_OPCION
-- ---------------------------------------------------------------------
INSERT INTO SEOPC_OPCION (
    SEOPC_CODIGO, SEOPC_NOMBRE, SEOPC_MODULO, SEOPC_OPERAC,
    SEOPC_DESCRI, SEOPC_ACTIVO, SESIS_CODIGO, SEEST_CODIGO
)
SELECT 'REP', 'Reportes', 'Personal', 'Reportes',
       'Generacion de reportes en PDF y Excel para departamentos, cargos y personal',
       1, 'SG', 'A'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM SEOPC_OPCION WHERE SEOPC_CODIGO = 'REP'
);

-- ---------------------------------------------------------------------
-- 2. Concesion del permiso OPL004 (REP) al perfil ADM en SEOXP_OPCPER
-- ---------------------------------------------------------------------
INSERT INTO SEOXP_OPCPER (
    SEOXP_CODIGO, SEOXP_AMBITO, SEOXP_VIGENT, SEOXP_DESDE, SEOXP_HASTA,
    SEPER_CODIGO, SEOPC_CODIGO, SEUSU_CODIGO
)
SELECT 'OPL004', 'Generacion de reportes en PDF y Excel (departamentos, cargos y personal)',
       1, NOW(), NULL,
       up.SEPER_CODIGO, 'REP', up.SEUSU_CODIGO
FROM SEUXP_USUPER up
WHERE up.SEPER_CODIGO = 'ADM'
  AND up.SEUXP_VIGENT = 1
  AND up.SEUXP_HASTA IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM SEOXP_OPCPER
      WHERE SEOPC_CODIGO = 'REP' AND SEPER_CODIGO = 'ADM'
  )
  AND NOT EXISTS (
      SELECT 1 FROM SEOXP_OPCPER WHERE SEOXP_CODIGO = 'OPL004'
  )
ORDER BY up.SEUXP_CODIGO
LIMIT 1;
