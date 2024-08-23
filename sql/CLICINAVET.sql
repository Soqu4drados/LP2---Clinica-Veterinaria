CREATE DATABASE IF NOT EXISTS clinicaVet;
USE clinicaVet;

CREATE TABLE IF NOT EXISTS veterinario(
    registro varchar(6) UNIQUE,
    cpf varchar(14) PRIMARY KEY,
    nome varchar(100) NOT NULL,
    datadenasc date,
    logradouro varchar(100)
);

CREATE TABLE IF NOT EXISTS tutor(
    cpf varchar(14) PRIMARY KEY,
    nome varchar(100) NOT NULL,
    datadenasc date,
    logradouro varchar(100)    
);

CREATE TABLE IF NOT EXISTS ANIMAL(
    cpfTutor varchar(14) NOT NULL,
    codigo int PRIMARY KEY AUTO_INCREMENT,
    nomeAnimal varchar(100) NOT NULL,
    raca varchar(50),
    datadenasc date,
    FOREIGN KEY (cpfTutor) REFERENCES tutor(cpf)
    ON UPDATE CASCADE ON DELETE CASCADE    
);

CREATE TABLE IF NOT EXISTS agendamento(
    codigoAgendamento int AUTO_INCREMENT PRIMARY KEY,
    dataagendamento DATE,
    horaagendamento TIME,
    veterinario varchar(100) NOT NULL,
    animal int NOT NULL,
    CONSTRAINT uc_data_hora UNIQUE (dataagendamento, horaagendamento),
    FOREIGN KEY (veterinario) REFERENCES veterinario(registro)
    ON UPDATE CASCADE ON DELETE CASCADE 
);

ALTER TABLE agendamento ADD 
CONSTRAINT FOREIGN KEY (animal) REFERENCES animal(codigo)
 ON UPDATE CASCADE ON DELETE CASCADE ;

-- Exemples:
-- Tabela Veterinario
INSERT INTO veterinario (registro, cpf, nome, datadenasc, logradouro) 
VALUES ('123ABC', '123.456.789-00', 'João Silva', '1980-05-15', 'Rua das Flores');
INSERT INTO veterinario (registro, cpf, nome, datadenasc, logradouro) 
VALUES ('456XYZ', '987.654.321-00', 'Maria Souza', '1975-10-20', 'Avenida Central');
INSERT INTO veterinario (registro, cpf, nome, datadenasc, logradouro) 
VALUES ('789DEF', '111.222.333-44', 'Pedro Santos', '1990-08-25', 'Praça Principal');

-- Tabela Tutor
INSERT INTO tutor (cpf, nome, datadenasc, logradouro) 
VALUES ('123.456.789-00', 'Ana Silva', '1985-07-10', 'Rua das Flores');
INSERT INTO tutor (cpf, nome, datadenasc, logradouro) 
VALUES ('987.654.321-00', 'Carlos Souza', '1970-11-25', 'Avenida Central');
INSERT INTO tutor (cpf, nome, datadenasc, logradouro) 
VALUES ('111.222.333-44', 'Mariana Santos', '1992-04-15', 'Praça Principal');

-- Tabela Animal
INSERT INTO ANIMAL (cpfTutor, codigo, nomeAnimal, raca, datadenasc) 
VALUES ('123.456.789-00', 1, 'Fido', 'Labrador', '2018-05-10');
INSERT INTO ANIMAL (cpfTutor, codigo, nomeAnimal, raca, datadenasc) 
VALUES ('987.654.321-00', 2, 'Mia', 'Persa', '2019-03-15');
INSERT INTO ANIMAL (cpfTutor, codigo, nomeAnimal, raca, datadenasc) 
VALUES ('111.222.333-44', 3, 'Bolinha', 'Vira-lata', '2017-08-20');

-- Tabela Agendamento
INSERT INTO agendamento (dataagendamento, horaagendamento, veterinario, animal) 
VALUES ('2024-07-15', '10:00:00', '123ABC', 1);

INSERT INTO agendamento (dataagendamento, horaagendamento, veterinario, animal) 
VALUES ('2024-07-20', '14:30:00', '456XYZ', 2);

INSERT INTO agendamento (dataagendamento, horaagendamento, veterinario, animal) 
VALUES ('2024-07-25', '11:00:00', '789DEF', 3);


