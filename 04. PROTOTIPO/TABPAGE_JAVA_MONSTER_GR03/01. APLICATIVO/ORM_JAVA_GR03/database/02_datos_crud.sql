-- =====================================================================
-- CRUD_JAVA_MONSTER_GR03 - Datos adicionales para probar el CRUD de departamentos
-- Ejecutar despues de 01_sigh_gr3.sql. Agrega personal para la opcion "Ver personal".
-- =====================================================================
USE SIGH_GR3;

INSERT IGNORE INTO PEDEP_DEPART (PEDEP_CODIGO, PEDEP_NOMBRE, PEDEP_ACTIVO) VALUES
('D04', 'Laboratorio Clinico', 1),
('D05', 'Farmacia', 0);

INSERT IGNORE INTO PEPER_PERSON (PEPER_CODIGO, PEPER_CODEXT, PEPER_NOMBRE, PEPER_APELLI, PEPER_FUNCIO, PEPER_ACTIVO,
    PEPER_TIPDOC, PEPER_NUMDOC, PEPER_FECNAC, PEPER_TELEFO, PEPER_CORREO, PEGEN_CODIGO, PEEST_CODIGO, PEDEP_CODIGO, PECAR_CODIGO) VALUES
('P00003', 'EXT0000003', 'Carla', 'Benitez', 'Medico de guardia', 1, 'CEDULA', '1700000003', '1988-02-14', '0990000003', 'carla.benitez@sigh.local', 'F', 'C', 'D02', 'C03'),
('P00004', 'EXT0000004', 'Mateo', 'Salazar', 'Enfermero de triaje', 1, 'CEDULA', '1700000004', '1996-07-21', '0990000004', 'mateo.salazar@sigh.local', 'M', 'S', 'D02', 'C02'),
('P00005', 'EXT0000005', 'Sofia', 'Andrade', 'Medico de consulta', 1, 'CEDULA', '1700000005', '1990-11-03', '0990000005', 'sofia.andrade@sigh.local', 'F', 'S', 'D03', 'C03'),
('P00006', 'EXT0000006', 'Diego', 'Paredes', 'Analista de laboratorio', 1, 'CEDULA', '1700000006', '1993-05-09', '0990000006', 'diego.paredes@sigh.local', 'M', 'C', 'D04', 'C01');
