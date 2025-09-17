export type ArtifactType = 'IMAGE' | 'LOG' | 'JSON_DATA';

export interface Artifact {
  id: string;
  type: ArtifactType;
  content: string;
  loggedAt?: string;
  name?: string;
  size?: number;
}

export type StepStatus = 'RUNNING' | 'COMPLETED' | 'FAILED';

export interface Step {
  id: string;
  name: string;
  status: StepStatus;
  artifacts: Artifact[];
  description?: string;
  startTime?: string;
  endTime?: string | null;
  duration?: number | null;
}

export interface Execution {
  id: string;
  steps: Step[];
  startedAt?: string;
  finishedAt?: string | null;
}

