import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';
import { Task, TaskRequest, AuditLog } from './models/task.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  providers: [ApiService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Task Manager Dashboard';
  currentView: 'tasks' | 'logs' = 'tasks';
  
  // Task data
  tasks: Task[] = [];
  currentPage = 0;
  totalPages = 0;
  totalItems = 0;
  pageSize = 5;
  searchQuery = '';
  
  // Audit logs
  auditLogs: AuditLog[] = [];
  
  // Modal state
  showModal = false;
  isEditMode = false;
  currentTask: Task | null = null;
  
  // Form data
  taskForm: TaskRequest = {
    title: '',
    description: ''
  };
  
  // Validation errors
  formErrors = {
    title: '',
    description: ''
  };

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadTasks();
  }

  // View switching
  switchView(view: 'tasks' | 'logs') {
    this.currentView = view;
    if (view === 'logs') {
      this.loadAuditLogs();
    }
  }

  // Load tasks
  loadTasks() {
    this.apiService.getTasks(this.currentPage, this.pageSize, this.searchQuery).subscribe({
      next: (response) => {
        this.tasks = response.tasks;
        this.currentPage = response.currentPage;
        this.totalPages = response.totalPages;
        this.totalItems = response.totalItems;
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        alert('Failed to load tasks. Please check your connection.');
      }
    });
  }

  // Search tasks
  onSearch() {
    this.currentPage = 0;
    this.loadTasks();
  }

  // Pagination
  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTasks();
    }
  }

  // Open modal for creating task
  openCreateModal() {
    this.isEditMode = false;
    this.currentTask = null;
    this.taskForm = { title: '', description: '' };
    this.formErrors = { title: '', description: '' };
    this.showModal = true;
  }

  // Open modal for editing task
  openEditModal(task: Task) {
    this.isEditMode = true;
    this.currentTask = task;
    this.taskForm = {
      title: task.title,
      description: task.description
    };
    this.formErrors = { title: '', description: '' };
    this.showModal = true;
  }

  // Close modal
  closeModal() {
    this.showModal = false;
    this.taskForm = { title: '', description: '' };
    this.formErrors = { title: '', description: '' };
  }

  // Validate form
  validateForm(): boolean {
    let isValid = true;
    this.formErrors = { title: '', description: '' };

    // Sanitize and validate title
    const title = this.sanitizeInput(this.taskForm.title);
    if (!title || title.trim() === '') {
      this.formErrors.title = 'Title is required';
      isValid = false;
    } else if (title.length > 100) {
      this.formErrors.title = 'Title must not exceed 100 characters';
      isValid = false;
    }

    // Sanitize and validate description
    const description = this.sanitizeInput(this.taskForm.description);
    if (!description || description.trim() === '') {
      this.formErrors.description = 'Description is required';
      isValid = false;
    } else if (description.length > 500) {
      this.formErrors.description = 'Description must not exceed 500 characters';
      isValid = false;
    }

    return isValid;
  }

  // Sanitize input to prevent XSS
  sanitizeInput(input: string): string {
    if (!input) return '';
    return input
      .replace(/<[^>]*>/g, '')
      .replace(/javascript:/gi, '')
      .replace(/on\w+=/gi, '')
      .trim();
  }

  // Save task (create or update)
  saveTask() {
    if (!this.validateForm()) {
      return;
    }

    const sanitizedTask: TaskRequest = {
      title: this.sanitizeInput(this.taskForm.title),
      description: this.sanitizeInput(this.taskForm.description)
    };

    if (this.isEditMode && this.currentTask) {
      // Update task
      this.apiService.updateTask(this.currentTask.id, sanitizedTask).subscribe({
        next: () => {
          this.loadTasks();
          this.closeModal();
        },
        error: (error) => {
          console.error('Error updating task:', error);
          alert('Failed to update task');
        }
      });
    } else {
      // Create task
      this.apiService.createTask(sanitizedTask).subscribe({
        next: () => {
          this.loadTasks();
          this.closeModal();
        },
        error: (error) => {
          console.error('Error creating task:', error);
          alert('Failed to create task');
        }
      });
    }
  }

  // Delete task
  deleteTask(task: Task) {
    if (confirm(`Are you sure you want to delete "${task.title}"?`)) {
      this.apiService.deleteTask(task.id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error deleting task:', error);
          alert('Failed to delete task');
        }
      });
    }
  }

  // Load audit logs
  loadAuditLogs() {
    this.apiService.getAuditLogs().subscribe({
      next: (logs) => {
        this.auditLogs = logs;
      },
      error: (error) => {
        console.error('Error loading audit logs:', error);
        alert('Failed to load audit logs');
      }
    });
  }

  // Get action color class
  getActionColor(action: string): string {
    if (action.includes('Create')) return 'action-create';
    if (action.includes('Update')) return 'action-update';
    if (action.includes('Delete')) return 'action-delete';
    return '';
  }

  // Format updated content for display
  formatUpdatedContent(content: any): string {
    if (!content) return '–';
    return Object.entries(content)
      .map(([key, value]) => `${key}: "${value}"`)
      .join(', ');
  }

  // Format date
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}