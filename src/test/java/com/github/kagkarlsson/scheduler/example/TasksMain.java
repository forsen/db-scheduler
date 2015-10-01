package com.github.kagkarlsson.scheduler.example;

import com.github.kagkarlsson.scheduler.*;
import com.github.kagkarlsson.scheduler.task.FixedDelay;
import com.github.kagkarlsson.scheduler.task.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.RecurringTask;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksMain {
	private static final Logger LOG = LoggerFactory.getLogger(TasksMain.class);

	public static void main(String[] args) throws Throwable {
		try {
			final HsqlTestDatabaseRule hsqlRule = new HsqlTestDatabaseRule();
			hsqlRule.before();
			final DataSource dataSource = hsqlRule.getDataSource();

			recurringTask(dataSource);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

	private static void recurringTask(DataSource dataSource) {

		final MyHourlyTask hourlyTask = new MyHourlyTask();

		final Scheduler scheduler = Scheduler
				.create(dataSource, Lists.newArrayList(hourlyTask))
				.build();

		// Schedule the task for execution now()
		scheduler.scheduleForExecution(LocalDateTime.now(), hourlyTask.instance("INSTANCE_1"));
	}

	public static class MyHourlyTask extends RecurringTask {

		public MyHourlyTask() {
			super("task_name", FixedDelay.of(Duration.ofHours(1)));
		}

		@Override
		public void execute(TaskInstance taskInstance) {
			System.out.println("Executed!");
		}
	}

	public static class MyAdhocTask extends OneTimeTask {

		public MyAdhocTask() {
			super("adhoc_task_name");
		}

		@Override
		public void execute(TaskInstance taskInstance) {
			System.out.println("Executed!");
		}
	}

	private static void adhocExecution(Scheduler scheduler, MyAdhocTask myAdhocTask) {

		// Schedule the task for execution a certain time in the future
		scheduler.scheduleForExecution(LocalDateTime.now().plusMinutes(5), myAdhocTask.instance("1045"));
	}

}
