import numpy as np
from PIL import Image

# import sys
# sys.path.append('../../src_code/pc')
from amlnn import engine


def get_image_do_preprocess(path):
    img = Image.open(path).convert('RGB').resize((224, 224))
    img = np.array(img)
    img = img / 127.5

    data = np.expand_dims(img,axis=0)
    data = data / 0.007843137718737125 + 1
    data = np.array(data).astype(np.uint8)

    return data

def postprocess_top5(predictions):
    predictions = predictions.reshape(-1)
    top5_indices = np.argsort(predictions)[::-1][:5]
    top5_scores = predictions[top5_indices]
    print("Top5 result with scores:")
    for idx, score in zip(top5_indices, top5_scores):
        print("Index: {}, Score: {}".format(idx, score))


if __name__ == "__main__":

    amlnn = engine.AMLNN(
        device_type='android',                                              # device type, ['android','linux']
        model_type='tensorflow',                                            # framework type, ['tensorflow','']
        model_path='./model_file/DDK6.4.8.7/image_classify_99_S905D3.nb',   # The nb file path in the pc : image_classify_99_S905D3.nb
        model_input_size='1 224 224 3',                                     # model input shape info, if multi-input, you can use '#' to split
        model_input_format='uint8',                                         # model input layer's data type, ['int8','uint8','int16','float32'], if multi-input, you can use '#' to split
        output_type='fp32',                                                 # output_type info, ['fp32','raw'], raw: raw result, fp32: float32 result
        run_cycles=1,                                                       # inference cycles, default: 1, means you will call inference once
        need_runtime=True,                                                  # if need runtime info, ['True','False']
        loglevel='INFO'                                                     # log level, ['DEBUG','INFO','WARNING','ERROR']
    )

    amlnn.init()

    # If it's a multi-input model, say there are 4 inputs, input0, input1, input2, input3, 
    # then the inputs are given like this: input_data = np.concatenate((data0,data1,data2,data3))
    input_data = get_image_do_preprocess('./dataset/input/224x224x3_fish.jpeg')
    amlnn.set_input(input_data)

    output_data = amlnn.inference()

    print("output number: ", len(output_data))
    print("output shape: ", output_data[0].shape)
    # If it is a multi-output model, say there are 4 outputs,
    # then the outputs should be taken like this: output_data[0], output_data[1], output_data[2], output_data[3]
    postprocess_top5(output_data[0])

    amlnn.destroy()