export interface RouteMetadata {
  title: string;
  description: string;
  keywords: string;
  ogTitle: string;
  ogDescription: string;
  ogType: string;
  twitterCard: string;
  twitterTitle: string;
  twitterDescription: string;
}

export const ROUTE_METADATA = {
  SIGN_IN: {
    title: 'Sign In - Listify',
    description: 'Sign in to your Listify account to manage your tasks and stay organized.',
    keywords: 'sign in, login, listify, todo, tasks, productivity',
    ogTitle: 'Sign In - Listify',
    ogDescription: 'Sign in to your Listify account to manage your tasks and stay organized.',
    ogType: 'website',
    twitterCard: 'summary',
    twitterTitle: 'Sign In - Listify',
    twitterDescription: 'Sign in to your Listify account to manage your tasks and stay organized.',
  } as RouteMetadata,

  SIGN_UP: {
    title: 'Sign Up - Listify',
    description:
      'Create a new Listify account to start organizing your tasks and boosting your productivity.',
    keywords: 'sign up, register, create account, listify, todo, tasks, productivity',
    ogTitle: 'Sign Up - Listify',
    ogDescription:
      'Create a new Listify account to start organizing your tasks and boosting your productivity.',
    ogType: 'website',
    twitterCard: 'summary',
    twitterTitle: 'Sign Up - Listify',
    twitterDescription:
      'Create a new Listify account to start organizing your tasks and boosting your productivity.',
  } as RouteMetadata,

  YOUR_TASKS: {
    title: 'Your Tasks - Listify',
    description:
      'Manage and organize your tasks with Listify. Create, edit, and track your todo items to stay productive.',
    keywords: 'tasks, todo, listify, productivity, organize, manage, todo list, task management',
    ogTitle: 'Your Tasks - Listify',
    ogDescription:
      'Manage and organize your tasks with Listify. Create, edit, and track your todo items to stay productive.',
    ogType: 'website',
    twitterCard: 'summary',
    twitterTitle: 'Your Tasks - Listify',
    twitterDescription:
      'Manage and organize your tasks with Listify. Create, edit, and track your todo items to stay productive.',
  } as RouteMetadata,
} as const;
