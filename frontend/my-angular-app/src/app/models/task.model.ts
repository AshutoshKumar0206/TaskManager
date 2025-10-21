export interface Task {
  id: string;
  title: string;
  description: string;
  createdAt: string;
}

export interface TaskRequest {
  title: string;
  description: string;
}

export interface TaskResponse {
  tasks: Task[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

export interface AuditLog {
  id: string;
  timestamp: string;
  action: string;
  taskId: string;
  updatedContent: { [key: string]: any } | null;
  notes: string | null;
}