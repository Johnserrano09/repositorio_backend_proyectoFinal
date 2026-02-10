-- V2__create_projects_table.sql
-- Projects table for programmer portfolios

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    project_type VARCHAR(20) NOT NULL,
    role_in_project VARCHAR(50),
    technologies TEXT[], -- Array of technology tags
    repo_url VARCHAR(500),
    demo_url VARCHAR(500),
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_project_type CHECK (project_type IN ('ACADEMIC', 'WORK')),
    CONSTRAINT chk_project_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX idx_projects_user_id ON projects(user_id);
CREATE INDEX idx_projects_type ON projects(project_type);
CREATE INDEX idx_projects_status ON projects(status);
