/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.android.gms.common.images.WebImage;
import com.robotium.solo.Solo;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ClientException;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.ScratchConversionManager;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.ScratchProgramDetailsActivity;
import org.catrobat.catroid.ui.adapter.ScratchProgramAdapter;
import org.catrobat.catroid.ui.fragment.ScratchConverterSlidingUpPanelFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.WebconnectionException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ScratchConverterActivityTest extends BaseActivityInstrumentationTestCase<ScratchConverterActivity> {

	private ScratchDataFetcher fetcherMock;
	private ScratchSearchResult defaultProgramsSearchResult;
	private List<ScratchProgramData> expectedDefaultProgramsList;
	private ConversionManager conversionManager;
	private Client clientMock;

	public ScratchConverterActivityTest() {
		super(ScratchConverterActivity.class);
	}

	private ScratchConverterSlidingUpPanelFragment getSlidingUpPanelFragment() {
		ScratchConverterActivity activity = (ScratchConverterActivity) solo.getCurrentActivity();
		return activity.getConverterSlidingUpPanelFragment();
	}

	@Override
	public void setUp() throws Exception {
		// prepare mocks
		final Uri firstImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png");
		expectedDefaultProgramsList = new ArrayList<ScratchProgramData>() {
			{
				add(new ScratchProgramData(10205819, "Program 1", "Owner 1", new WebImage(firstImageURL, 150, 150)));
				add(new ScratchProgramData(10205820, "Program 2", "Owner 2", null));
				add(new ScratchProgramData(10205821, "Program 3", "Owner 3", null));
				add(new ScratchProgramData(10205822, "Program 4", "Owner 4", null));
			}
		};
		defaultProgramsSearchResult = new ScratchSearchResult(expectedDefaultProgramsList, "", 0);

		fetcherMock = Mockito.mock(ScratchDataFetcher.class);
		when(fetcherMock.fetchDefaultScratchPrograms()).thenReturn(defaultProgramsSearchResult);

		clientMock = Mockito.mock(Client.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				conversionManager = (ConversionManager) invocation.getArguments()[0];
				return null;
			}
		}).when(clientMock).connectAndAuthenticate(any(ScratchConversionManager.class));
		when(clientMock.isAuthenticated()).thenReturn(false);

		ScratchConverterActivity.setDataFetcher(fetcherMock);
		ScratchConverterActivity.setClient(clientMock);
		super.setUp();
		solo.sleep(100);
		verify(clientMock, times(1)).setConvertCallback(any(Client.ConvertCallback.class));
		verify(clientMock, times(1)).isAuthenticated();
		verify(clientMock, times(1)).connectAndAuthenticate(any(ScratchConversionManager.class));
		verifyNoMoreInteractions(clientMock);
	}

	//------------------------------------------------------------------------------------------------------------------
	// Connect & authenticate tests
	//------------------------------------------------------------------------------------------------------------------
	public void testShouldSaveClientIDToSharedPreferencesAfterSuccessfullyConnectedAndAuthenticated() {
		// store invalid client ID in shared preferences
		final long oldClientID = Client.INVALID_CLIENT_ID;
		final SharedPreferences settings;
		settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Constants.SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME, oldClientID);
		editor.commit();

		final long expectedNewClientID = 123;

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onSuccess(expectedNewClientID);
		verify(clientMock, times(2)).isAuthenticated();

		// now check if clientID has been updated in shared preferences
		final long storedClientID = settings.getLong(Constants.SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME,
				Client.INVALID_CLIENT_ID);
		assertEquals("ClientID has not been saved to SharedPreferences", expectedNewClientID, storedClientID);
	}

	public void testShouldCallRetrieveInfoAfterSuccessfullyConnectedAndAuthenticated() {
		final long expectedNewClientID = 123;
		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onSuccess(expectedNewClientID);
		verify(clientMock, times(2)).isAuthenticated();
		verify(clientMock, times(1)).retrieveInfo();
		verifyNoMoreInteractions(clientMock);
	}

	public void testShouldDisplayUpdatePocketCodeDialogIfServersCatrobatLanguageVersionIsNewer() {
		final float serverCatrobatLanguageVersion = Constants.CURRENT_CATROBAT_LANGUAGE_VERSION + 0.01f;
		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(serverCatrobatLanguageVersion, new Job[] {});

		solo.sleep(200);

		assertTrue("Server's Catrobat Language version is newer than the app's version, but no warning dialog shown!",
				solo.searchText(getActivity().getString(R.string.error_scratch_converter_outdated_pocketcode_version)));
	}

	public void testShouldReturnToMainMenuActivityAndShowToastErrorMessageAfterServerClosedConnection() {
		assertTrue("Expecting ScratchConverterActivity to be current activity before connection is closed!",
				solo.getCurrentActivity() instanceof ScratchConverterActivity);
		assertEquals("Expecting ScratchConverterActivity to be current activity before connection is closed!",
				getActivity(), solo.getCurrentActivity());

		conversionManager.onConnectionClosed(new ClientException("Server closed sucessfully connection"));
		solo.waitForActivity("MainMenuActivity", 2_000);

		assertTrue("No or unexpected toast error message shown!", solo.searchText(
				getActivity().getString(R.string.connection_closed), true));
		assertTrue("Did not return to MainMenuActivity!", solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testUserPressedBackButtonShouldFinishActivityAndCloseConnectionAndShowToastErrorMessage() {
		when(clientMock.isClosed()).thenReturn(false);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				conversionManager.onConnectionClosed(new ClientException("Client closed connection!"));
				return null;
			}
		}).when(clientMock).close();

		assertTrue("Expecting ScratchConverterActivity to be current activity before connection is closed!",
				solo.getCurrentActivity() instanceof ScratchConverterActivity);
		assertEquals("Expecting ScratchConverterActivity to be current activity before connection is closed!",
				getActivity(), solo.getCurrentActivity());

		solo.goBack();
		solo.sleep(200);

		verify(clientMock, times(1)).isClosed();
		verify(clientMock, times(1)).close();
		verifyNoMoreInteractions(clientMock);

		assertTrue("No or unexpected toast error message shown!", solo.searchText(
				getActivity().getString(R.string.connection_closed), true));
		assertTrue("Did not finish activity!", getActivity().isFinishing());
	}

	//------------------------------------------------------------------------------------------------------------------
	// SlidePanel tests (SlidingUpPanelFragment)
	//------------------------------------------------------------------------------------------------------------------
	public void testSlidingUpPanelLayoutShouldBeHiddenAtTheBeginning() {
		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
		assertEquals("Sliding panel must be hidden after activity started",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());
		assertFalse("Fragment should not contain jobs at the beginning", getSlidingUpPanelFragment().hasVisibleJobs());
	}

	public void testShouldShowSlidePanelBarAfterJobsInfoEventReceivedWithJobs() {
		final Uri firstImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png");
		final Uri secondImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205821_480x360.png");

		final Job firstJob = new Job(10205819, "Program 1", new WebImage(firstImageURL, 480, 360));
		firstJob.setState(Job.State.FINISHED);

		final Job secondJob = new Job(10205821, "Program 2", new WebImage(secondImageURL, 480, 360));
		secondJob.setState(Job.State.RUNNING);

		final Job[] expectedJobs = new Job[] { firstJob, secondJob };

		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
		assertEquals("SlidingUpPanelBar should be hidden by default!",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, expectedJobs);

		solo.sleep(1_000);

		assertEquals("Must show SlidingUpPanelBar when there are jobs!",
				SlidingUpPanelLayout.PanelState.COLLAPSED, slidingLayout.getPanelState());
	}

	public void testSlidingUpPanelLayoutShouldBeShownAfterReceivedInfo() {
		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
		assertEquals("Sliding panel must be hidden after activity started",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());
		assertFalse("Fragment should not contain jobs at the beginning", getSlidingUpPanelFragment().hasVisibleJobs());
	}

	public void testShouldNotShowSlidePanelBarAfterJobsInfoEventReceivedWithNoJobs() {
		final Job[] expectedJobs = new Job[] {};

		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
		assertEquals("SlidingUpPanelBar should be hidden by default!",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, expectedJobs);

		solo.sleep(400);

		assertEquals("SlidingUpPanelBar should remain hidden when there are no jobs!",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());
	}

	public void testShouldNotShowSlidePanelBarAfterJobsInfoEventReceivedWithOnlyInvisibleUnscheduledJobs() {
		final Job job = new Job(10205819, "Program 1", null);
		job.setState(Job.State.UNSCHEDULED);

		final Job[] expectedJobs = new Job[] { job };

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, expectedJobs);

		solo.sleep(400);

		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
		assertEquals("Should not show SlidingUpPanelBar when there are no jobs!",
				SlidingUpPanelLayout.PanelState.HIDDEN, slidingLayout.getPanelState());
	}

	public void testShouldDisplayAllJobsAfterJobsInfoEventReceived() {
		final Uri firstImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png");
		final Uri secondImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205821_480x360.png");

		final Job firstJob = new Job(10205819, "Program 1", new WebImage(firstImageURL, 480, 360));
		firstJob.setState(Job.State.FINISHED);

		final Job secondJob = new Job(10205821, "Program 2", new WebImage(secondImageURL, 480, 360));
		secondJob.setState(Job.State.RUNNING);

		final Job[] expectedJobs = new Job[] { firstJob, secondJob };

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, expectedJobs);

		solo.sleep(400);

		final ScratchConverterActivity activity = (ScratchConverterActivity) solo.getCurrentActivity();

		final RelativeLayout runningJobsList = (RelativeLayout) activity.findViewById(R.id.scratch_conversion_list);
		final ListView runningJobsListView = (ListView) activity.findViewById(R.id.scratch_conversion_list_view);

		final RelativeLayout finishedFailedJobsList = (RelativeLayout) activity.findViewById(
				R.id.scratch_converted_programs_list);
		final ListView finishedFailedJobsListView = (ListView) activity.findViewById(
				R.id.scratch_converted_programs_list_view);

		assertTrue("Fragment does not contain any jobs", getSlidingUpPanelFragment().hasVisibleJobs());
		assertEquals("RunningJobsList must be visible", View.VISIBLE, runningJobsList.getVisibility());
		assertEquals("FinishedFailedJobsList must be visible", View.VISIBLE, finishedFailedJobsList.getVisibility());
		assertEquals("Number of list items of running jobs not as expected",
				1, runningJobsListView.getAdapter().getCount());
		assertEquals("Number of list items of finished or failed jobs not as expected",
				1, finishedFailedJobsListView.getAdapter().getCount());
	}

	public void testSlidePanelBarShouldBeExpandableAfterJobsInfoEventReceivedWithJobs() {
		final Uri firstImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png");
		final Job job = new Job(10205819, "Program 1", new WebImage(firstImageURL, 480, 360));
		job.setState(Job.State.FINISHED);

		final Job[] expectedJobs = new Job[] { job };

		when(clientMock.isAuthenticated()).thenReturn(true);
		conversionManager.onInfo(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION, expectedJobs);

		solo.sleep(1_000);

		final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) getActivity().findViewById(
				R.id.sliding_layout);

		assertEquals("Must show SlidingUpPanelBar when there are jobs!",
				SlidingUpPanelLayout.PanelState.COLLAPSED, slidingLayout.getPanelState());

		// now expand the panel bar by simply tapping on it
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
			}
		});

		solo.sleep(2_000);

		assertEquals("Must show SlidingUpPanelBar when there are jobs!",
				SlidingUpPanelLayout.PanelState.EXPANDED, slidingLayout.getPanelState());
	}

	//------------------------------------------------------------------------------------------------------------------
	// Search tests (SearchScratchSearchProjectsListFragment)
	//------------------------------------------------------------------------------------------------------------------
	public void testCheckIfAllDefaultProgramsArePresentAtTheBeginning() {
		SearchView searchView = (SearchView) solo.getCurrentActivity().findViewById(R.id.search_view_scratch);
		ListView searchListView = (ListView) solo.getCurrentActivity().findViewById(R.id.list_view_search_scratch);

		solo.sleep(200);

		assertEquals("Search view text must be empty at the beginning", 0, searchView.getQuery().length());
		assertEquals("Number of viewed default programs differs",
				expectedDefaultProgramsList.size(), searchListView.getAdapter().getCount());
		for (int index = 0; index < searchListView.getAdapter().getCount(); index++) {
			assertEquals("Default programs at index " + index + " differs from expected default program",
					expectedDefaultProgramsList.get(index), searchListView.getAdapter().getItem(index));
		}
	}

	public void testSearch() throws InterruptedIOException, WebconnectionException, InterruptedException {
		final SearchView searchView = (SearchView) solo.getCurrentActivity().findViewById(R.id.search_view_scratch);
		ListView searchListView = (ListView) solo.getCurrentActivity().findViewById(R.id.list_view_search_scratch);

		final String searchQuery = "test";

		ScratchSearchResult searchResult = new ScratchSearchResult(new ArrayList<ScratchProgramData>() {
			{
				add(new ScratchProgramData(1, "Program 1", "Owner 1", null));
				final Uri secondImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/2_480x360.png");
				add(new ScratchProgramData(2, "Program 2", "Owner 2", new WebImage(secondImageURL, 480, 360)));
			}
		}, searchQuery, 0);

		when(fetcherMock.scratchSearch(any(String.class), any(Integer.class), any(Integer.class)))
				.thenReturn(searchResult);

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				searchView.setQuery(searchQuery, true);
				synchronized (this) {
					notify();
				}
			}
		};
		synchronized (runnable) {
			getActivity().runOnUiThread(runnable);
			runnable.wait();
		}

		solo.sleep(200);

		assertEquals("Search view text must be empty at the beginning", searchQuery, searchView.getQuery().toString());
		assertEquals("Number of viewed default programs differs",
				searchResult.getProgramDataList().size(), searchListView.getAdapter().getCount());
		for (int index = 0; index < searchListView.getAdapter().getCount(); index++) {
			assertEquals("Default programs at index " + index + " differs from expected default program",
					searchResult.getProgramDataList().get(index), searchListView.getAdapter().getItem(index));
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	// List item tests
	//------------------------------------------------------------------------------------------------------------------
	public void testShouldOpenScratchProgramDetailsActivityIfClickedOnListViewItemOfAScratchProgram() {
		solo.clickOnText(expectedDefaultProgramsList.get(0).getTitle());
		solo.waitForActivity("ScratchProgramDetailsActivity", 2_000);

		assertTrue("Did not open ScratchProgramDetailsActivity!",
				solo.getCurrentActivity() instanceof ScratchProgramDetailsActivity);
	}

	public void testShouldShowToastErrorIfMoreThanMaxNumberOfAllowedProgramsSelectedToConvert() {
		solo.sendKey(Solo.MENU);
		solo.clickOnMenuItem(getActivity().getString(R.string.convert));

		solo.sleep(100);

		for (int index = 0; index < (Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT + 1); index++) {
			solo.clickOnCheckBox(index);
		}

		solo.sleep(200);

		assertTrue("No or unexpected toast error message shown", solo.searchText(
				getActivity().getResources().getQuantityString(
						R.plurals.error_cannot_convert_more_than_x_programs,
						Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
						Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT)));

		final ListView listView = UiTestUtils.getListView(solo, 0);
		final ScratchProgramAdapter scratchProgramAdapter = (ScratchProgramAdapter) listView.getAdapter();
		final Set<Integer> checkedProgramsIndex = scratchProgramAdapter.getCheckedPrograms();

		assertEquals("Could not select maximum number of allowed programs!",
				Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT, checkedProgramsIndex.size());

		final Set<Integer> expectedSet = new HashSet<>();
		expectedSet.add(0);
		expectedSet.add(1);
		expectedSet.add(2);

		for (int indexOfCheckedProgram : checkedProgramsIndex) {
			assertTrue("Program '" + expectedDefaultProgramsList.get(indexOfCheckedProgram).getTitle()
							+ "' at index: " + indexOfCheckedProgram + " is not checked",
					expectedSet.contains(indexOfCheckedProgram));
			expectedSet.remove(indexOfCheckedProgram);
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	// Convert program tests (SearchScratchSearchProjectsListFragment)
	//------------------------------------------------------------------------------------------------------------------
	public void testStartConversionOfSelectedProgram() {
		final ScratchProgramData expectedProgramData = expectedDefaultProgramsList.get(0);

		solo.sendKey(Solo.MENU);
		solo.clickOnMenuItem(getActivity().getString(R.string.convert));

		solo.sleep(100);
		solo.clickOnCheckBox(0);
		solo.sleep(200);

		final ListView listView = UiTestUtils.getListView(solo, 0);
		final ScratchProgramAdapter scratchProgramAdapter = (ScratchProgramAdapter) listView.getAdapter();
		final Set<Integer> checkedProgramsIndex = scratchProgramAdapter.getCheckedPrograms();

		assertEquals("Unable to select the program!", 1, checkedProgramsIndex.size());
		assertTrue("Program '" + expectedDefaultProgramsList.get(0).getTitle() + "' at index: 0 is not checked",
				checkedProgramsIndex.contains(0));

		assertTrue("Couldn't fetch overflow button", solo.waitForView(ActionMenuView.class, 1, 500));
		final ArrayList<ActionMenuView> actionMenuView = solo.getCurrentViews(ActionMenuView.class);
		assertFalse("Couldn't fetch overflow button", actionMenuView.isEmpty());

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertNotNull("Second argument of convertProgram() call must not be null",
						invocation.getArguments()[1]);
				assertNotNull("Third argument of convertProgram() call must not be null",
						invocation.getArguments()[2]);

				final long jobID = (long) invocation.getArguments()[0];
				final String jobTitle = (String) invocation.getArguments()[1];
				final WebImage jobImage = (WebImage) invocation.getArguments()[2];
				final boolean verbose = (boolean) invocation.getArguments()[3];
				final boolean force = (boolean) invocation.getArguments()[4];

				assertEquals("Unexpected job ID given! Should be Scratch program ID",
						expectedProgramData.getId(), jobID);
				assertEquals("Unexpected job title given! Should be Scratch program title",
						expectedProgramData.getTitle(), jobTitle);
				assertEquals("Unexpected job image given! Should be Scratch program screenshot image",
						expectedProgramData.getImage().getUrl().toString(), jobImage.getUrl().toString());
				assertFalse("Verbose mode is reserved for FUTURE releases and should remain disabled at the moment",
						verbose);
				assertFalse("Unexpected enabled force mode!", force);
				return null;
			}
		}).when(clientMock).convertProgram(anyLong(), anyString(), any(WebImage.class), anyBoolean(), anyBoolean());

		UiTestUtils.acceptAndCloseActionMode(solo);

		verify(clientMock, times(2)).getNumberOfJobsInProgress();
		verify(clientMock, times(1)).isJobInProgress(eq(expectedProgramData.getId()));
		verify(clientMock, times(1)).convertProgram(anyLong(), anyString(), any(WebImage.class),
				anyBoolean(), anyBoolean());
		verifyNoMoreInteractions(clientMock);
	}
}
