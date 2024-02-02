(require '[net.cassiel.reveal-bb.render :as r])
(import '[java.io.File])

(letfn [(ce [item] [:span.colour-emph item])
        (it [item] [:span {:style "font-style: italic;"} item])
        (max-obj [item]
          [:span {:style (clojure.string/join ";" ["background-color: #404040"
                                                   "color: #C0C0C0"
                                                   "font-family: monospace"
                                                   "font-size: 0.8em"
                                                   "padding-left: 5px"
                                                   "padding-right: 5px"
                                                   "border-width: 7px 3px"
                                                   "border-style: solid"
                                                   "border-color: #606060"
                                                   "border-radius: 0px"
                                                   ""])}
           item])]
  (r/render :template "../template.html"
            :reveal-location (-> (System/getProperty "user.home")
                                 (File. "GITHUB")
                                 (File. "cassiel")
                                 (File. "reveal.js"))

            :theme :black
            :title "Touch Boards and Max"
            :author "Nick Rothwell"
            :out-dir "."

            :slides [[:section
                    [:h3 "Touch Boards and Max"]
                    [:h4 "Nick Rothwell"]]

                   [:section
                    [:h3 "Overview and Concepts"]

                    [:ul
                     [:li "Touch Boards and MIDI messages"]
                     [:li "Max and Media: audio and video playback"]
                     [:li "Integration: Touch Board as controller"]]
                    ]

                   [:section
                    [:h3 "Touch Boards and MIDI"]

                    [:ul
                     [:li "MIDI is a 40-year-old communication protocol: low-speed, numbers between 0 and 127"]
                     [:li "Touch Boards with MIDI sketches (" (r/tt "Midi_interface") ", "
                      (r/tt "Midi_interface_generic") ") can operate as MIDI " (it "controllers")
                      ", generating MIDI messages"]]
                    ]

                   [:section
                    [:h3 "MIDI Messages"]

                    [:ul
                     [:li "Discrete messages, on/off: example, notes being played on a (piano) keyboard"]
                     [:li "Continuous messages, 0 to 127: example, volume control knob"]]]

                   [:section
                    [:h3 "MIDI into Max"]

                    [:ul
                     [:li "Max supports both note and controller messages"]]

                    (r/image-h 200 "max-ctlin-notein.jpg")

                    [:ul
                     [:li "The Touch Board can send note messages (touch) or controller messages (proximity) - or both"]]
                    ]

                   [:section
                    [:h3 "Note Input"]
                    (r/image-h 200 "max-note-input.jpg")

                    [:p "The numeric values are (right to left):"]

                    [:ul
                     [:li "MIDI channel (1 to 16): denotes which device is sending (or receiving)"]
                     [:li "Velocity of note - corresponds to loudness, always 127 from Touch Board"]
                     [:li "Pitch (note number): 48 upwards from Touch Board"]]
                    ]

                   [:section
                    [:h3 "Discriminating Between Values"]

                    [:ul
                     [:li "We want to treat the Touch Board inputs individually"]
                     [:li "We can do this using a " (max-obj "select") " object to pick out the pitch values"]]

                    (r/image-h 200 "max-select.jpg")
                    ]

                   [:section
                    [:h3 "Audio Playback"]

                    (r/image-h 350 "max-playlist.jpg")]

                   [:section
                    [:h3 "Audio Playback"]

                    [:ul
                     [:li "The audio 'jukebox' object is called " (max-obj "playlist~")]
                     [:li "You have to connect up audio yourself: the output object is called " (max-obj "ezdac~")]
                     [:li "Each " (max-obj "playlist~") " can play one file at a time. If you want more, "
                      "add another playlist"]]
                    ]

                   [:section
                    [:h3 "Video Playback"]

                    (r/image-h 350 "max-jit-playlist.jpg")]

                   [:section
                    [:h3 "Video Playback"]

                    [:ul
                     [:li "Very similar to audio. The object is " (max-obj "jit.playlist")]
                     [:li "We don't want to hear the audio tracks on video; disable by setting the "
                      (r/tt "vol") " attribute to 0"]
                     [:li "Display video in a separate window with " (max-obj "jit.window")]
                     [:li (max-obj "jit.window") " has a " (r/tt "fullscreen") " attribute to project full-screen"]]]

                   [:section
                    [:h3 "Controlling playback regions"]

                    [:p "Both objects accept a " (r/tt "selection") " message to set the playback selection"]

                    [:ul
                     [:li "First number: which file to set (starting from 1)"]
                     [:li "Second number: start of selection (0.0 to 1.0)"]
                     [:li "Third number: end of selection (0.0 to 1.0)"]]]

                   [:section
                    [:h3 "Final Exercise: Linking Up"]

                    [:p "Attach the buttons that are triggered by the Touch Boards to the buttons"
                     " which change the selection ranges"]

                    (r/image-h 300 "hookup.jpg")
                    ]

                   [:section
                    [:h3 "Linking Up"]

                    [:ul
                     [:li "In the illustration, the Touch Board bangs the " (it "yellow buttons")]
                     [:li "The media patcher responds to the " (it "red buttons")]
                     [:li "Merge the code into one patcher, make the touch board change the audio and video selections"]]]
                   ]
          ))
