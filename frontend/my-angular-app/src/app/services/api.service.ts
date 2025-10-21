import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskRequest, TaskResponse, AuditLog } from '../models/task.model';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = `${environment.apiUrl}`;
  ngOnInit() {
    console.log('API URL:', this.apiUrl);
  }
  private username = 'admin';
  private password = 'password123';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const credentials = btoa(`${this.username}:${this.password}`);
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Basic ${credentials}`
    });
  }

  getTasks(page: number = 0, size: number = 5, search: string = ''): Observable<TaskResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<TaskResponse>(`${this.apiUrl}/api/tasks`, {
      headers: this.getHeaders(),
      params
    });
  }

  createTask(task: TaskRequest): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/api/tasks`, task, {
      headers: this.getHeaders()
    });
  }

  updateTask(id: string, task: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/api/tasks/${id}`, task, {
      headers: this.getHeaders()
    });
  }

  deleteTask(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/tasks/${id}`, {
      headers: this.getHeaders()
    });
  }

  getAuditLogs(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.apiUrl}/api/logs`, {
      headers: this.getHeaders()
    });
  }
}