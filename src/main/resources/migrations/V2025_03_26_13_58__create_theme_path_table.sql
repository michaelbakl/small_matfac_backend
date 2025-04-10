create table if not exists theme
(
    theme_id UUID PRIMARY KEY,
    path LTREE NOT NULL, -- Иерархический путь (например: "5_класс.числа.дроби")
    name VARCHAR(100) NOT NULL, -- Отображаемое имя (например: "дроби")
    level INT NOT NULL -- Уровень вложенности (для удобства)
);

CREATE INDEX idx_theme_path_gist ON question_themes USING GIST(path);
CREATE INDEX idx_theme_path_btree ON question_themes USING BTREE(path);
