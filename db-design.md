# DB設計書

## 1. 文書の目的

ToDoアプリで使用するデータベース（データを保存する仕組み）の構造を定義する。

対象テーブルは、要件どおり MySQL の `todos` 1つだけとする。1件のToDoを、`todos` の1行に保存する。

## 2. データベースの前提

| 項目 | 内容 |
|---|---|
| データベース | MySQL |
| 対象テーブル | `todos` のみ |
| 利用者 | 1人（ログインなし） |
| 文字コード | `utf8mb4`（日本語など幅広い文字を保存できる文字コード） |
| 主キー | `id`（1行を一意に識別する番号） |

## 3. テーブル一覧

| テーブル名 | 役割 |
|---|---|
| `todos` | やること、メモ、ジャンル、優先度、期限、完了状態を保存する |

## 4. `todos` テーブル定義

### 4.1 カラム一覧

カラム（テーブルの縦の項目）は次のとおりとする。

| カラム名 | データ型 | NULL（空の値） | 初期値 | キー・制約 | 内容 |
|---|---|---|---|---|---|
| `id` | `BIGINT` | 不可 | 自動採番 | `PRIMARY KEY`、`AUTO_INCREMENT` | ToDoを識別する番号。保存時に自動で1ずつ増える |
| `title` | `VARCHAR(255)` | 不可 | なし | 255文字以内 | やること。必須入力 |
| `detail` | `VARCHAR(255)` | 可 | `NULL` | 255文字以内 | メモ。未入力を許可 |
| `category` | `VARCHAR(255)` | 不可 | なし | `CHECK`で5種類に制限 | ジャンル。必須入力 |
| `priority` | `INT` | 不可 | `2` | `CHECK`で1〜3に制限 | 優先度。1=高、2=中、3=低 |
| `due_date` | `DATE` | 可 | `NULL` | なし | 期限。未入力を許可 |
| `completed` | `BOOLEAN` | 不可 | `FALSE` | 0または1 | 完了状態。`FALSE`（未完了）または`TRUE`（完了） |
| `created_at` | `DATETIME` | 不可 | 現在日時 | `DEFAULT CURRENT_TIMESTAMP` | 登録日時。自動設定 |
| `updated_at` | `DATETIME` | 不可 | 現在日時 | `ON UPDATE CURRENT_TIMESTAMP` | 最終更新日時。登録時・更新時に自動設定 |

### 4.2 ジャンルの許可値

`category` は自由入力にせず、次の5つだけを保存する。

- `デザイン`
- `マーケティング`
- `プログラミング`
- `資格`
- `就職活動`

### 4.3 優先度と完了状態の対応

| カラム | 保存値 | 画面表示 |
|---|---|---|
| `priority` | `1` | 高 |
| `priority` | `2` | 中 |
| `priority` | `3` | 低 |
| `completed` | `0`（`FALSE`） | 未完了 |
| `completed` | `1`（`TRUE`） | 完了 |

## 5. 要件とカラムの対応

| 要件 | 対応するカラム | 保存・利用方法 |
|---|---|---|
| やることを登録する | `title` | 登録・編集で保存する。空白だけの入力はアプリ側で空として扱い、保存しない |
| メモを登録する | `detail` | 未入力なら`NULL`、入力時は255文字以内で保存する |
| ジャンルを選ぶ | `category` | 5つの固定値のいずれかを保存する |
| 優先度を選ぶ | `priority` | 1〜3で保存する。未指定時のDB初期値は2（中） |
| 期限を登録する | `due_date` | 未入力なら`NULL`。一覧の並び替えで使用する |
| 完了状態を変更する | `completed` | 未完了は`FALSE`、完了は`TRUE`で保存する |
| ToDoを識別する | `id` | 編集・削除のURL `/todos/{id}/...` で使用する |
| 登録日時を保存する | `created_at` | MySQLが自動設定する。画面入力は行わない |
| 更新日時を保存する | `updated_at` | MySQLが登録・更新時に自動設定する。画面入力は行わない |

## 6. 検索・並び替えとカラムの対応

| 機能 | 使用カラム | 条件 |
|---|---|---|
| やることの名前で検索 | `title` | 入力文字を一部でも含むToDoを残す（部分一致） |
| ジャンルで検索 | `category` | 指定したジャンルと完全に一致するToDoを残す |
| 期限が近い順 | `due_date` | 期限の昇順（小さい日付から並べる） |
| 期限が遠い順 | `due_date` | 期限の降順（大きい日付から並べる） |
| 全件表示 | `title`, `category` | 検索条件が空の場合は絞り込まない |

期限が未入力（`NULL`）のToDoをどの位置に置くかは、画面側の検索処理で仕様に合わせて扱う。

## 7. CREATE TABLE文（DDL）

DDL（テーブルを作成するSQL）は次のとおりとする。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_todos_category
        CHECK (category IN ('デザイン', 'マーケティング', 'プログラミング', '資格', '就職活動')),
    CONSTRAINT chk_todos_priority
        CHECK (priority IN (1, 2, 3))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
```

`PRIMARY KEY`（主キー）は行を重複なく見分けるための設定、`NOT NULL`（空を許可しない）は必須項目の設定、`CHECK`（値の検査）は許可されない値を保存しないための設定である。

## 8. 画面に表示しないカラム

次のカラムは内部管理用であり、登録・編集の入力欄には表示しない。

- `id`
- `created_at`
- `updated_at`

## 9. ログ要件について

要件定義書には、登録・編集・削除の成功時に「操作の種類」と対象の`id`を1操作1行で記録する要件がある。一方、今回の前提では使用テーブルは`todos`の1つだけであり、ログ用テーブルは作成しない。

そのため、このDB設計ではログ保存用のカラムやテーブルを追加しない。ログの保存場所（アプリケーションログなど）と、利用者を識別する方法は未確定事項として、別途決定する。

なお、ログには`title`と`detail`の値を記録しない。

## 10. 対象外

今回のDB設計では、次のデータは保存しない。

- ログイン情報・利用者情報
- 複数利用者の共有情報
- 完了日時
- 削除済みToDoの復元用データ
- 写真・ファイルの添付情報

