-- =====================================================================
-- LISTBOX_conMVC_JAVA_MONSTER_GR03 - Datos semilla y opcion CAR
-- Ejecutar despues de 01_sigh_gr3.sql (y opcionalmente 02_datos_crud.sql).
-- 100 % idempotente y compatible con MariaDB 10.4 (XAMPP) y MySQL 8.
-- =====================================================================
USE SIGH_GR3;

-- 1. Cargos semilla adicionales en PECAR_CARGO (sin alterar el DDL oficial)
INSERT IGNORE INTO PECAR_CARGO (PECAR_CODIGO, PECAR_NOMBRE, PECAR_ACTIVO) VALUES
('C04', 'Medico especialista', 1),
('C05', 'Tecnologo de laboratorio', 1),
('C06', 'Farmaceutico hospitalario', 1),
('C07', 'Auxiliar administrativo', 0);

-- 2. Registro de la opcion CAR (Cargos) en SEOPC_OPCION
INSERT INTO SEOPC_OPCION (
    SEOPC_CODIGO, SEOPC_NOMBRE, SEOPC_MODULO, SEOPC_OPERAC,
    SEOPC_DESCRI, SEOPC_ACTIVO, SESIS_CODIGO, SEEST_CODIGO
)
SELECT 'CAR', 'Cargos', 'Personal', 'Mantenimiento',
       'Consulta con ListBox, filtros y mantenimiento de cargos', 1, 'SG', 'A'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM SEOPC_OPCION WHERE SEOPC_CODIGO = 'CAR'
);

-- 3. Concesion de la opcion CAR al perfil de la primera asignacion vigente (ADM)
INSERT INTO SEOXP_OPCPER (
    SEOXP_CODIGO, SEOXP_AMBITO, SEOXP_VIGENT, SEOXP_DESDE, SEOXP_HASTA,
    SEPER_CODIGO, SEOPC_CODIGO, SEUSU_CODIGO
)
SELECT 'OPL002', 'Mantenimiento de cargos con ListBox y filtros', 1, NOW(), NULL,
       up.SEPER_CODIGO, 'CAR', up.SEUSU_CODIGO
FROM SEUXP_USUPER up
WHERE up.SEUXP_VIGENT = 1 AND up.SEUXP_HASTA IS NULL
  AND NOT EXISTS (SELECT 1 FROM SEOXP_OPCPER WHERE SEOPC_CODIGO = 'CAR')
  AND NOT EXISTS (SELECT 1 FROM SEOXP_OPCPER WHERE SEOXP_CODIGO = 'OPL002')
ORDER BY up.SEUXP_CODIGO
LIMIT 1;
