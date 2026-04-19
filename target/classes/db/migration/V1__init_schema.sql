-- 1. AppUser Table
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL -- 1: User, 2: Manager, 3: Admin
);

-- 2. GithubProfile Table (1-to-1 with AppUser)
CREATE TABLE github_profiles (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

-- 3. GithubRepo Table (1-to-N with GithubProfile)
CREATE TABLE github_repos (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255),
    profile_id BIGINT,
    CONSTRAINT fk_profile FOREIGN KEY(profile_id) REFERENCES github_profiles(id) ON DELETE CASCADE
);

-- 4. GithubIssue Table (1-to-N with GithubRepo)
CREATE TABLE github_issues (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    repo_id BIGINT,
    CONSTRAINT fk_repo FOREIGN KEY(repo_id) REFERENCES github_repos(id) ON DELETE CASCADE
);

-- 5. Tag Table (N-to-M with GithubRepo)
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 6. Join Table for Repo <-> Tags (Many-to-Many)
CREATE TABLE repo_tags (
    repo_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (repo_id, tag_id),
    CONSTRAINT fk_repo_tags_repo FOREIGN KEY(repo_id) REFERENCES github_repos(id) ON DELETE CASCADE,
    CONSTRAINT fk_repo_tags_tag FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Insert Default Admin User (Password is 'default' hashed with BCrypt)
INSERT INTO app_users (email, password, role_id) 
VALUES ('admin@default.com', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 3);