export type Task = {
  id: string;
  title: string;
  ownerId: string;
  description: string;
  date: Date;
  cover: Cover;
  createdAt: Date;
  completedAt: null;
  completed: boolean;
};

export type Cover = {
  uri: string;
  thumbnailUri: string;
  blurhash: string;
  state: string;
};

export type GetTasksResponse = Task[];

export type GetTaskResponse = Task;

export type CreateTaskResponse = Task;

export type UpdateTaskResponse = Task;

export type CompleteTaskResponse = Task;
