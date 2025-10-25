CREATE TABLE IF NOT EXISTS plays (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  text TEXT
);

INSERT INTO plays (title, text) VALUES
('Hamlet', 'To be, or not to be: that is the question.'),
('Macbeth', 'Out, damned spot! out, I say!'),
('Othello', 'O, beware, my lord, of jealousy; It is the green-eyed monster.'),
('Richard III', 'A horse, a horse, my kingdom for a horse!'),
('Anthony and Cleopatra', 'O happy horse, to bear the weight of Antony!'),
('Julius Caesar', 'Yond Cassius has a lean and hungry look; He thinks too much: such men are dangerous'),
('Henry IV', 'Uneasy lies the head that wears a crown'),
('Henry V', 'Once more unto the breach, dear friends, once more');
