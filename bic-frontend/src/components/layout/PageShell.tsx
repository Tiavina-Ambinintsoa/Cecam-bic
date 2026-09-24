import type { ReactNode } from "react";

interface PageShellProps {
  titre: string;

  description?: string;

  actions?: ReactNode;
  children: ReactNode;
}

export function PageShell({ titre, description, actions, children }: PageShellProps) {
  return (
    <div className="mx-auto w-full max-w-5xl px-6 py-10">
      <header className="flex flex-wrap items-start justify-between gap-4 border-b border-border pb-5">
        <div className="max-w-xl">
          <h1 className="font-heading text-2xl text-foreground">{titre}</h1>
          {description && (
            <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{description}</p>
          )}
        </div>
        {actions && <div className="flex shrink-0 gap-2">{actions}</div>}
      </header>
      <div className="pt-8">{children}</div>
    </div>
  );
}