-- MySQL Migration Toolkit
 -- SQL Create Script


SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `MLTestBedExperiments`;

USE `MLTestBedExperiments`;

 -- Tables

DROP TABLE IF EXISTS `MLTestBedExperiments`.`results`;
CREATE TABLE `MLTestBedExperiments`.`results` (
  `ExpNum` INT(10) NOT NULL,
  `RunNum` INT(10) NOT NULL,
  `Iteration` BIGINT(19) NOT NULL,
  `Swarm` INT(10) NOT NULL DEFAULT 0,
  `Particle` INT(10) NOT NULL,
  `BestScore` FLOAT(53) NOT NULL,
  `CurrentScore` FLOAT(53) NOT NULL,
  `isBest` TINYINT NULL,
  `Position` LONGTEXT NULL,
  `BestPosition` LONGTEXT NULL,
  `Velocity` LONGTEXT NULL,
  `ExpSpecific` LONGTEXT NULL,
  PRIMARY KEY (`ExpNum`, `RunNum`, `Iteration`, `Swarm`, `Particle`),
  CONSTRAINT `FK_results_notebook` FOREIGN KEY `FK_results_notebook` (`ExpNum`)
    REFERENCES `MLTestBedExperiments`.`notebook` (`ExpNum`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = INNODB;

DROP TABLE IF EXISTS `MLTestBedExperiments`.`swarms`;
CREATE TABLE `MLTestBedExperiments`.`swarms` (
  `ExpNum` INT(10) NOT NULL,
  `RunNum` INT(10) NOT NULL,
  `SwarmNo` INT(10) NOT NULL,
  `Params` LONGTEXT NULL,
  PRIMARY KEY (`ExpNum`, `RunNum`, `SwarmNo`),
  CONSTRAINT `FK_swarms_notebook` FOREIGN KEY `FK_swarms_notebook` (`ExpNum`)
    REFERENCES `MLTestBedExperiments`.`notebook` (`ExpNum`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = INNODB;

DROP TABLE IF EXISTS `MLTestBedExperiments`.`notebook`;
CREATE TABLE `MLTestBedExperiments`.`notebook` (
  `ExpNum` INT(10) NOT NULL,
  `Description` LONGTEXT NOT NULL,
  `Parameters` LONGTEXT NOT NULL,
  `Notes` LONGTEXT NULL,
  `StartTime` DATETIME NULL,
  `EndTime` DATETIME NULL,
  PRIMARY KEY (`ExpNum`)
)
ENGINE = INNODB;

-- --------- -
-- Views

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`ParticlePositions`;
 CREATE VIEW ParticlePositions
 AS
 SELECT     ExpNum, RunNum, Swarm, Iteration, Particle, isBest, CurrentScore, BestScore, Position, Velocity, BestPosition
 FROM         results;

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`Experiments`;
 CREATE VIEW Experiments
 AS
 SELECT     notebook.ExpNum, notebook.Description, notebook.Parameters, notebook.Notes, results.RunNum, results.Iteration,
                       results.Particle, results.BestScore, results.CurrentScore, results.isBest
 FROM         notebook INNER JOIN
                       results ON notebook.ExpNum = results.ExpNum;

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`Exppos`;
 CREATE VIEW Exppos
 AS
 SELECT      ExpNum, RunNum, Swarm, Iteration, Particle, isBest, CurrentScore, Position, BestPosition
 FROM         ParticlePositions A
 WHERE     (Particle BETWEEN 10 AND 20 OR
                       Particle = - 1) AND (Iteration BETWEEN - 1 AND 100 OR
                       Iteration = 1000 OR
                       Iteration = 1500 OR
                       Iteration = 2999 OR
                       Iteration = 250 OR
                       Iteration = 500 OR
                       Iteration = 750 OR
                       Iteration IN
                           (SELECT     MAX(Iteration)
                             FROM          results B
                             WHERE      B.ExpNum = A.ExpNum)) OR
                       (Iteration BETWEEN - 1 AND 100) OR
                       (Iteration = 1000) OR
                       (Iteration = 1500) OR
                       (Iteration = 2999) OR
                       (Iteration = 250) OR
                       (Iteration = 500) OR
                       (Iteration = 750) OR
                       (Iteration IN
                           (SELECT     MAX(Iteration)
                             FROM          results B
                             WHERE      B.ExpNum = A.ExpNum)) OR
                       (Iteration IN
                           (SELECT     (Iteration)
                             FROM          results B
                             WHERE      B.ExpNum = A.ExpNum
                             GROUP BY B.Iteration
                             HAVING      MIN(currentscore) = MIN(bestscore))) AND (isBest = 1)
 ORDER BY ExpNum, RunNum, Iteration, Particle;

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`ExpIter`;
 CREATE VIEW ExpIter
 AS
 SELECT    Exppos.ExpNum, Exppos.RunNum, Exppos.Iteration, Exppos.Swarm, Exppos.Particle, Exppos.isBest, results.BestScore,
                       Exppos.CurrentScore, Exppos.Position, Exppos.BestPosition, results.Velocity, results.ExpSpecific
 FROM         Exppos Exppos INNER JOIN
                       results results ON Exppos.ExpNum = results.ExpNum AND Exppos.RunNum = results.RunNum AND Exppos.Iteration = results.Iteration AND
                       Exppos.Particle = results.Particle
 WHERE     (Exppos.isBest = 1)
 ORDER BY results.ExpNum, Exppos.RunNum, Exppos.Iteration, Exppos.Swarm, Exppos.Particle;

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`MaxExpIter`;
 CREATE VIEW MaxExpIter
 AS
 SELECT     *
 FROM         results
 WHERE     (isBest = 1) AND (Particle = - 1) AND (ExpNum IN
                           (SELECT     MAX(ExpNum) AS Expr1
                             FROM          notebook))
 ORDER BY ExpNum, Swarm, Iteration;

 DROP VIEW IF EXISTS `MLTestBedExperiments`.`IterPerExpPerParticle`;
 CREATE VIEW IterPerExpPerParticle
 AS
 SELECT  ExpNum, COUNT(*) AS 'Count'
 FROM         results
 GROUP BY ExpNum, RunNum, Swarm, Particle
 ORDER BY ExpNum, RunNum, Swarm, Particle;



SET FOREIGN_KEY_CHECKS = 1;


-- EOF
