from python_speech_features import *
from scipy.io import wavfile

def get_feature(filename, feature_type):
	fs, signal = wavfile.read(filename)

	if feature_type == 'mfcc':
		return mfcc(signal, fs)
	if feature_type == 'fbank':
		return logfbank(signal, fs)
	if feature_type == 'ssc':
		return ssc(signal, fs)
	return None
	
