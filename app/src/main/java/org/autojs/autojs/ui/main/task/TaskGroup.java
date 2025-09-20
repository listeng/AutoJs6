package org.autojs.autojs.ui.main.task;

import static org.autojs.autojs.util.StringUtils.str;

import android.content.Context;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import org.autojs.autojs.AutoJs;
import org.autojs.autojs.execution.ScriptExecution;
import org.autojs.autojs.timing.IntentTask;
import org.autojs.autojs.timing.TimedTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs6.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on Nov 28, 2017.
 */
public abstract class TaskGroup implements Parent<Task> {

    protected List<Task> mTasks = new ArrayList<>();
    private final String mTitle;

    protected TaskGroup(String title) {
        mTitle = title;
    }

    @Override
    public List<Task> getChildList() {
        return mTasks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }

    public String getTitle() {
        return mTitle;
    }

    public abstract Context getContext();

    public abstract void refresh();

    public static class PendingTaskGroup extends TaskGroup {

        private final Context mContext;

        public PendingTaskGroup(Context context) {
            super(context.getString(R.string.text_timed_task));
            mContext = context;
            refresh();
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        public void refresh() {
            mTasks.clear();
            for (TimedTask timedTask : TimedTaskManager.getAllTasksAsList()) {
                mTasks.add(new Task.PendingTask(getContext(), timedTask));
            }
            for (IntentTask intentTask : TimedTaskManager.getAllIntentTasksAsList()) {
                mTasks.add(new Task.PendingTask(getContext(), intentTask));
            }
            sortPendingTasks();
        }

        public int addTask(Object task) {
            Task.PendingTask pendingTask;
            if (task instanceof TimedTask) {
                pendingTask = new Task.PendingTask(getContext(), (TimedTask) task);
            } else if (task instanceof IntentTask) {
                pendingTask = new Task.PendingTask(getContext(), (IntentTask) task);
            } else {
                throw new IllegalArgumentException(str(R.string.error_illegal_argument, "task", task));
            }
            int insertIndex = getInsertIndex(pendingTask);
            mTasks.add(insertIndex, pendingTask);
            return insertIndex;
        }

        public int removeTask(Object data) {
            int i = indexOf(data);
            if (i >= 0)
                mTasks.remove(i);
            return i;
        }

        private int indexOf(Object data) {
            for (int i = 0; i < mTasks.size(); i++) {
                Task.PendingTask task = (Task.PendingTask) mTasks.get(i);
                if (task.taskEquals(data)) {
                    return i;
                }
            }
            return -1;
        }

        public UpdateResult updateTask(Object task) {
            int i = indexOf(task);
            if (i < 0) {
                return UpdateResult.notFound();
            }

            Task.PendingTask pendingTask = (Task.PendingTask) mTasks.remove(i);
            if (task instanceof TimedTask) {
                pendingTask.setTimedTask((TimedTask) task);
            } else if (task instanceof IntentTask) {
                pendingTask.setIntentTask((IntentTask) task);
            } else {
                throw new IllegalArgumentException(str(R.string.error_illegal_argument, "task", task));
            }

            int newIndex = getInsertIndex(pendingTask);
            mTasks.add(newIndex, pendingTask);
            return UpdateResult.of(i, newIndex);
        }

        private int getInsertIndex(Task.PendingTask pendingTask) {
            long target = pendingTask.getNextRunAtMillis();
            for (int idx = 0; idx < mTasks.size(); idx++) {
                Task.PendingTask existing = (Task.PendingTask) mTasks.get(idx);
                long existingTarget = existing.getNextRunAtMillis();
                if (Long.compare(target, existingTarget) < 0) {
                    return idx;
                }
            }
            return mTasks.size();
        }

        private void sortPendingTasks() {
            mTasks.sort((task1, task2) -> {
                Task.PendingTask pending1 = (Task.PendingTask) task1;
                Task.PendingTask pending2 = (Task.PendingTask) task2;
                return Long.compare(pending1.getNextRunAtMillis(), pending2.getNextRunAtMillis());
            });
        }

        public static class UpdateResult {
            private final boolean mFound;
            private final int mFromIndex;
            private final int mToIndex;

            private UpdateResult(boolean found, int fromIndex, int toIndex) {
                mFound = found;
                mFromIndex = fromIndex;
                mToIndex = toIndex;
            }

            public static UpdateResult notFound() {
                return new UpdateResult(false, -1, -1);
            }

            public static UpdateResult of(int fromIndex, int toIndex) {
                return new UpdateResult(true, fromIndex, toIndex);
            }

            public boolean isFound() {
                return mFound;
            }

            public boolean isMoved() {
                return mFound && mFromIndex != mToIndex;
            }

            public int getFromIndex() {
                return mFromIndex;
            }

            public int getToIndex() {
                return mToIndex;
            }
        }
    }

    public static class RunningTaskGroup extends TaskGroup {

        private final Context mContext;

        public RunningTaskGroup(Context context) {
            super(context.getString(R.string.text_running_task));
            mContext = context;
            refresh();
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        public void refresh() {
            mTasks.clear();
            AutoJs.getInstance().getScriptEngineService().getScriptExecutions().forEach(this::addTask);
        }

        public int addTask(ScriptExecution engine) {
            // @Overwrite by SuperMonster003 on Apr 1, 2023.
            //  ! On homepage of AutoJs6, if we run a script
            //  ! and then switch from FILE tab to TASK tab rapidly,
            //  ! there will be two tasks of the same instance in the list.
            //  ! Checking its existence before invoking `mTasks.add` solved the problem.
            //  ! zh-CN:
            //  ! 在 AutoJs6 主页运行一个脚本,
            //  ! 此时如果迅速从 "文件" 标签页切换到 "任务" 标签页,
            //  ! 列表中会出现同一个实例的两个相同的任务.
            //  ! 在调用 `mTasks.add` 之前检查其存在性可解决此问题.
            //  !
            //  # int pos = mTasks.size();
            //  # mTasks.add(new Task.RunningTask(getContext(), engine));
            //  # return pos;

            int size = mTasks.size();
            if (size == 0 || indexOf(engine) == -1) {
                mTasks.add(new Task.RunningTask(getContext(), engine));
                return size;
            }
            return -1;
        }

        public int removeTask(ScriptExecution engine) {
            int i = indexOf(engine);
            if (i != -1) mTasks.remove(i);
            return i;
        }

        public int indexOf(ScriptExecution engine) {
            for (int i = 0; i < mTasks.size(); i++) {
                if (((Task.RunningTask) mTasks.get(i)).getScriptExecution().equals(engine)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
