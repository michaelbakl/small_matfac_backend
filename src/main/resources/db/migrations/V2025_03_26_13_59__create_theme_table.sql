-- Подключаем расширение ltree
CREATE EXTENSION IF NOT EXISTS ltree;

create table if not exists theme
(
    themeId UUID PRIMARY KEY,
    path ltree, -- Иерархический путь (например: "5_класс.числа.дроби")
    name VARCHAR(100) NOT NULL, -- Отображаемое имя (например: "дроби")
    level INT NOT NULL -- Уровень вложенности (для удобства)
);

ALTER TABLE theme ADD COLUMN IF NOT EXISTS path ltree;

COMMENT ON COLUMN theme.path IS 'Путь темы (ltree)';


CREATE INDEX idx_theme_path_gist ON theme USING GIST(path);
CREATE INDEX idx_theme_path_btree ON theme USING BTREE(path);
