package mugres.common;

public enum Controller {
    Bank_Select(0, "Bank Select"),
    Modulation_Wheel(1, "Modulation Wheel"),
    Breath_controller(2, "Breath controller"),
    Foot_Pedal(4, "Foot Pedal"),
    Portamento_Time(5, "Portamento Time"),
    Data_Entry(6, "Data Entry"),
    Volume(7, "Volume"),
    Balance(8, "Balance"),
    Pan_position(10, "Pan position"),
    Expression(11, "Expression"),
    Effect_Control_1(12, "Effect Control 1"),
    Effect_Control_2(13, "Effect Control 2"),
    General_Purpose_1(16, "General Purpose 1"),
    General_Purpose_2(17, "General Purpose 2"),
    General_Purpose_3(18, "General Purpose 3"),
    General_Purpose_4(19, "General Purpose 4"),
    Controller_0(32, "Controller 0"),
    Controller_1(33, "Controller 1"),
    Controller_2(34, "Controller 2"),
    Controller_3(35, "Controller 3"),
    Controller_4(36, "Controller 4"),
    Controller_5(37, "Controller 5"),
    Controller_6(38, "Controller 6"),
    Controller_7(39, "Controller 7"),
    Controller_8(40, "Controller 8"),
    Controller_9(41, "Controller 9"),
    Controller_10(42, "Controller 10"),
    Controller_11(43, "Controller 11"),
    Controller_12(44, "Controller 12"),
    Controller_13(45, "Controller 13"),
    Controller_14(46, "Controller 14"),
    Controller_15(47, "Controller 15"),
    Controller_16(48, "Controller 16"),
    Controller_17(49, "Controller 17"),
    Controller_18(50, "Controller 18"),
    Controller_19(51, "Controller 19"),
    Controller_20(52, "Controller 20"),
    Controller_21(53, "Controller 21"),
    Controller_22(54, "Controller 22"),
    Controller_23(55, "Controller 23"),
    Controller_24(56, "Controller 24"),
    Controller_25(57, "Controller 25"),
    Controller_26(58, "Controller 26"),
    Controller_27(59, "Controller 27"),
    Controller_28(60, "Controller 28"),
    Controller_29(61, "Controller 29"),
    Controller_30(62, "Controller 30"),
    Controller_31(63, "Controller 31"),
    Hold_Pedal(64, "Hold Pedal (on/off)"),
    Portamento(65, "Portamento (on/off)"),
    Sostenuto_Pedal(66, "Sostenuto Pedal (on/off)"),
    Soft_Pedal(67, "Soft Pedal (on/off)"),
    Legato_Pedal(68, "Legato Pedal (on/off)"),
    Hold2_Pedal(69, "Hold 2 Pedal (on/off)"),
    Sound_Variation(70, "Sound Variation"),
    Resonance(71, "Resonance (Timbre)"),
    Sound_Release_Time(72, "Sound Release Time"),
    Sound_Attack_Time(73, "Sound Attack Time"),
    Frequency_Cutoff(74, "Frequency Cutoff (Brightness)"),
    Sound_Control_6(75, "Sound Control 6"),
    Sound_Control_7(76, "Sound Control 7"),
    Sound_Control_8(77, "Sound Control 8"),
    Sound_Control_9(78, "Sound Control 9"),
    Sound_Control_10(79, "Sound Control 10"),
    Decay_General_Purpose_Button1(80, "Decay / General Purpose Button 1 (on/off)"),
    Hi_Pass_Filter_Frequency_General_Purpose_Button2(81, "Hi Pass Filter Frequency / General Purpose Button 2 (on/off)"),
    General_Purpose_Button_3(82, "General Purpose Button 3 (on/off)"),
    General_Purpose_Button_4(83, "General Purpose Button 4 (on/off)"),
    Portamento_Amount(84, "Portamento Amount"),
    Reverb_Level(91, "Reverb Level"),
    Tremolo_Level(92, "Tremolo Level"),
    Chorus_Level(93, "Chorus Level"),
    Detune_Level(94, "Detune Level"),
    Phaser_Level(95, "Phaser Level"),
    Data_Button_Increment(96, "Data Button increment"),
    Data_Button_Decrement(97, "Data Button decrement"),
    All_Sound_Off(120, "All Sound Off"),
    All_Controllers_Off(121, "All Controllers Off"),
    Local_Keyboard(122, "Local Keyboard (on/off)"),
    All_Notes_Off(123, "All Notes Off"),
    Omni_Mode_Off(124, "Omni Mode Off"),
    Omni_Mode_On(125, "Omni Mode On"),
    Mono_Operation(126, "Mono Operation"),
    Poly_Mode(127, "Poly Mode");

    private int id;
    private final String label;

    Controller(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int id() {
        return id;
    }

    public String label() {
        return label;
    }
}
