-- V4__create_advisories_table.sql
-- Advisories (asesorías) between programmers and external users

CREATE TABLE advisories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    programmer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    external_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_comment TEXT,
    response_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_advisory_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_advisories_programmer_id ON advisories(programmer_id);
CREATE INDEX idx_advisories_external_id ON advisories(external_id);
CREATE INDEX idx_advisories_status ON advisories(status);
CREATE INDEX idx_advisories_scheduled_at ON advisories(scheduled_at);
