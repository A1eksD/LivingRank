export interface AdminUser {
  id: string;
  email: string;
  displayName: string;
  authProvider: string;
  emailVerified: boolean;
  profileImageUrl?: string;
  role: string;
  status: string;
  suspendedAt?: string;
  suspendedReason?: string;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AdminReview {
  id: number;
  streetId: number;
  streetName: string;
  streetCity: string;
  userId: string;
  userEmail: string;
  userDisplayName: string;
  overallRating: number;
  comment?: string;
  visible: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AdminMail {
  id: number;
  adminId: string;
  adminDisplayName: string;
  recipientId: string;
  recipientEmail: string;
  recipientDisplayName: string;
  subject: string;
  body: string;
  hasDeadline: boolean;
  deadlineAction?: string;
  sentAt: string;
}

export interface AuditLog {
  id: number;
  adminId: string;
  adminDisplayName: string;
  action: string;
  targetType: string;
  targetId: string;
  details?: string;
  ipAddress: string;
  createdAt: string;
}

export interface ScheduledAction {
  id: number;
  adminId: string;
  adminDisplayName: string;
  targetUserId: string;
  targetUserEmail: string;
  targetUserDisplayName: string;
  actionType: string;
  reason?: string;
  deadline: string;
  executed: boolean;
  executedAt?: string;
  cancelled: boolean;
  cancelledAt?: string;
  relatedMailId?: number;
  createdAt: string;
}

export interface AdminDashboard {
  totalUsers: number;
  activeUsers: number;
  suspendedUsers: number;
  totalReviews: number;
  hiddenReviews: number;
  pendingScheduledActions: number;
  totalStreets: number;
}

export interface AdminMailRequest {
  recipientId: string;
  subject: string;
  body: string;
  deadline?: string;
  deadlineAction?: string;
}

export interface AdminUserUpdateRequest {
  displayName?: string;
  email?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
