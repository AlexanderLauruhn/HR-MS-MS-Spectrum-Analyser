# HR-MS-MS-Spectrum-Analyser
This software removes those signals from a high-resolution tandem mass spectrum, which are mathematically clearly identifyable as false-positive signals, probable caused by impurities in the sample or by incorrect calibration of the instrument.
# How to use the software
The picture shows a screenshot of the graphical user interface.
<img src=".\Picture\image.png">
Open your mass spectrum using the button "import File" (1). This should by available as .txt or .csv file. The spectrum will be shown as plot (2) and in the table (3).<br>
After that, type the molecular sum of your target compound (4). Optionally, you can choose a minimum molecular sum in the textfield above (5), which you want to match. Choose the ionisation type (6) and the deviation you want to allow for matching (7, unit is part per million). If you want to calculate based on multiple ionisations, enter the ionisation count (8).<br>
If you select "remove implausiblities" (9), the software will not generate molecular data of chemically implausible fragments like C5H2 or CO3.<br>
Finally, choose the color (10), in which you like to see the matched signals of your spectrum. You can choose different colors for different calculations.<br>
After clicking on "calculate" (11), the upper table (12) will show all calculated molecules, which theoretically can be a fragment of your compount. The table below (3) now shows you the matches based on the molecular formula (4 and 5)  and the choosen deviatation. The matched signals now turned to your choosen color. Finally, you can save the spectrum without non-machted signals using the "export File" button (13).
# What the software does
Based on the given molecular formula, the algorithm generates a list of all molecular formular of fragments, that can exist of your compound, and have at least the atoms of your minimum formula. After that, the software compares all signals of your spectrum with each of the fragment masses. Matches are shown in the table and shown in the figure with the selected color.

